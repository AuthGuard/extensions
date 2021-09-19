package com.nexblocks.authguard.external.email;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.nexblocks.authguard.config.ConfigContext;
import com.nexblocks.authguard.external.email.config.ImmutableSendGridConfig;
import com.nexblocks.authguard.service.exceptions.ConfigurationException;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SendgridEmailProvider implements EmailProvider {
    private static final String MESSAGE_ID_HEADER = "X-Message-ID";

    private static final Logger LOG = LoggerFactory.getLogger(SendgridEmailProvider.class);

    private final SendGrid sendGrid;
    private final ImmutableSendGridConfig config;

    @Inject
    public SendgridEmailProvider(@Named("sendgrid") final ConfigContext configContext) {
        this(configContext.asConfigBean(ImmutableSendGridConfig.class));
    }

    public SendgridEmailProvider(final ImmutableSendGridConfig config) {
        if (config.getApiKeyVariable() == null) {
            throw new ConfigurationException("Required configuration property sendgrid.apiKeyVariable was not set");
        }

        if (config.getFromEmailAddress() == null) {
            throw new ConfigurationException("Required configuration property sendgrid.fromEmailAddress");
        }

        final String apiKey = System.getenv(config.getApiKeyVariable());

        if (apiKey == null) {
            throw new ConfigurationException("Environment variable " + config.getApiKeyVariable() + " was not set");
        }

        this.sendGrid = new SendGrid(apiKey);
        this.config = config;
    }

    @Override
    public void send(final ImmutableEmail email) {
        final String templateId = config.getEmailTemplates().get(email.getTemplate());

        if (templateId == null) {
            LOG.error("Template {} is not mapped to a SendGrid template", email.getTemplate());
        }

        final Personalization recipient = recipientPersonalization(email);

        final Mail mail = new Mail();

        mail.setFrom(new Email(config.getFromEmailAddress(), config.getFromName()));
        mail.addPersonalization(recipient);
        mail.setTemplateId(templateId);

        doSend(mail);
    }

    private Personalization recipientPersonalization(final ImmutableEmail email) {
        final Personalization recipient = new Personalization();

        recipient.addTo(new Email(email.getTo()));

        for (String key : email.getParameters().keySet()) {
            recipient.addDynamicTemplateData(key, email.getParameters().get(key));
        }

        return recipient;
    }

    private void doSend(final Mail mail) {
        try {
            final Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);

            if (response.getStatusCode() != 202) {
                LOG.error("Received response code other than 202: {} {}",
                        response.getStatusCode(), response.getBody());
            } else {
                LOG.info("Sent email {}", response.getHeaders().get(MESSAGE_ID_HEADER));
            }
        } catch (final IOException e) {
            LOG.error("Failed call to SendGrid", e);
        }
    }
}
