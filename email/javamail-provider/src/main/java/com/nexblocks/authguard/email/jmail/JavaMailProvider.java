package com.nexblocks.authguard.email.jmail;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.nexblocks.authguard.config.ConfigContext;
import com.nexblocks.authguard.email.jmail.config.ImmutableJavaMailProviderConfig;
import com.nexblocks.authguard.email.jmail.config.ImmutableTemplateConfig;
import com.nexblocks.authguard.external.email.EmailProvider;
import com.nexblocks.authguard.external.email.ImmutableEmail;
import com.nexblocks.authguard.external.email.EmailParametersHelper;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * An email provider implementation using JavaMail. It
 * can support any protocol JavaMail supports. Its
 * configuration is split into two parts:
 * 1. javaMail: A list of key-value pairs which will be
 *    mapped to a Properties object and will be passed
 *    to a JavaMail session.
 * 2. provider: The configuration of the provider which
 *    includes template file and subjects mapping, from
 *    address ..etc.
 *
 * It only supports Handlebars templates for now.
 */
public class JavaMailProvider implements EmailProvider {
    private static final Logger LOG = LoggerFactory.getLogger(JavaMailProvider.class);

    private static final String CONTENT_TYPE = "text/html; charset=UTF-8";

    private final TemplateResolver templateResolver;
    private final TemplatesLoader templatesLoader;
    private final Properties javaMailConfig;
    private final ImmutableJavaMailProviderConfig providerConfig;

    private Session mailSession;

    @Inject
    public JavaMailProvider(final @Named("mail") ConfigContext config) {
        this.templateResolver = new HandlebarsTemplateResolver();
        this.templatesLoader = new TemplatesLoader();

        this.javaMailConfig = config.getSubContext("javaMail").asProperties();
        this.providerConfig = config.getAsConfigBean("provider", ImmutableJavaMailProviderConfig.class);
    }

    public JavaMailProvider(final ImmutableJavaMailProviderConfig providerConfig, final Properties javaMailConfig) {
        this.templateResolver = new HandlebarsTemplateResolver();
        this.templatesLoader = new TemplatesLoader();

        this.javaMailConfig = javaMailConfig;
        this.providerConfig = providerConfig;
    }

    @Override
    public void send(final ImmutableEmail immutableEmail) {
        final ImmutableTemplateConfig templateConfig = Optional.ofNullable(providerConfig.getTemplates())
                .map(templates -> templates.get(immutableEmail.getTemplate()))
                .orElse(null);

        if (templateConfig == null) {
            LOG.error("Template {} is not mapped to a file", immutableEmail.getTemplate());
        } else {
            loadAndParseTemplate(immutableEmail, templateConfig)
                    .andThen(content -> doSend(immutableEmail, templateConfig.getSubject(), content));
        }
    }

    private Session getSession() {
        if (mailSession == null) {
            if (providerConfig.getUsername() != null) {
                mailSession = Session.getInstance(javaMailConfig, getAuthenticator());
            } else {
                mailSession = Session.getInstance(javaMailConfig);
            }
        }

        return mailSession;
    }

    private Authenticator getAuthenticator() {
        return new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(providerConfig.getUsername(), providerConfig.getPassword());
            }
        };
    }

    private Try<String> loadAndParseTemplate(final ImmutableEmail immutableEmail, final ImmutableTemplateConfig templateConfig) {
        final String templateFile = templateConfig.getFile();
        final Map<String, Object> parameters = EmailParametersHelper.combineParameters(providerConfig.getDefaultParameters(),
                immutableEmail.getParameters());

        LOG.debug("Template {} was mapped to file {}", immutableEmail.getTemplate(), templateFile);

        Try<String> contentTry = templatesLoader.get(templateFile, providerConfig.enableFileCache())
                .flatMap(template -> templateResolver.resolve(template, parameters));

        if (contentTry.isFailure()) {
            LOG.error("Failed to process template ({}, {})", immutableEmail.getTemplate(),
                    templateFile, contentTry.getCause());
        }

        return contentTry;
    }

    private void doSend(final ImmutableEmail immutableEmail, final String subject, final String content) {
        try {
            final MimeMessage msg = new MimeMessage(getSession());

            msg.setFrom(new InternetAddress(providerConfig.getFromAddress(), providerConfig.getFromName()));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(immutableEmail.getTo()));
            msg.setSubject(subject);
            msg.setContent(content, CONTENT_TYPE);

            Transport.send(msg);
        } catch (final Exception e) {
            LOG.error("Failed to send email", e);
        }
    }
}
