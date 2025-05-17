package com.nexblocks.authguard.email.jmail;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.nexblocks.authguard.email.jmail.config.ImmutableJavaMailProviderConfig;
import com.nexblocks.authguard.email.jmail.config.ImmutableTemplateConfig;
import com.nexblocks.authguard.external.email.ImmutableEmail;
import com.sun.mail.util.MailSSLSocketFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SmtpJavaMailProviderTest {

    private GreenMail greenMail;
    private JavaMailProvider javaMailProvider;
    private JavaMailProvider secureJavaMailProvider;
    private JavaMailProvider wrongCredentialsJavaMailProvider;
    private GreenMailMessagesVisitor messagesVisitor;

    @BeforeAll
    void setup() throws GeneralSecurityException {
        greenMail = new GreenMail(new ServerSetup[] {
                ServerSetupTest.SMTP,
                ServerSetupTest.SMTPS
        });

        greenMail.setUser("tester", "secure_password");
        greenMail.start();

        final ImmutableJavaMailProviderConfig emailProviderConfig = ImmutableJavaMailProviderConfig.builder()
                .username("tester")
                .password("secure_password")
                .fromAddress("noreply@test.com")
                .fromName("Unit Tests")
                .putTemplates("otp", ImmutableTemplateConfig.builder()
                        .file(getClass().getClassLoader().getResource("otp.hbs").getFile())
                        .subject("OTP email")
                        .build())
                .build();

        final Properties javaMailProperties = new Properties();
        final Properties secureJavaMailProperties = new Properties();

        final MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);

        javaMailProperties.setProperty("mail.smtp.host", "localhost");
        javaMailProperties.setProperty("mail.smtp.port", "" + greenMail.getSmtp().getPort());
        javaMailProperties.setProperty("mail.debug", "true");

        secureJavaMailProperties.setProperty("mail.smtp.host", "localhost");
        secureJavaMailProperties.setProperty("mail.smtp.port", "" + greenMail.getSmtps().getPort());
        secureJavaMailProperties.setProperty("mail.smtp.ssl.enable", "true");
        secureJavaMailProperties.setProperty("mail.smtp.auth", "true");
        secureJavaMailProperties.put("mail.smtp.ssl.socketFactory", sf);

        javaMailProvider = new JavaMailProvider(emailProviderConfig, javaMailProperties);
        secureJavaMailProvider = new JavaMailProvider(emailProviderConfig, secureJavaMailProperties);
        wrongCredentialsJavaMailProvider = new JavaMailProvider(emailProviderConfig.withPassword("wrong"), secureJavaMailProperties);
        messagesVisitor = new GreenMailMessagesVisitor(greenMail);
    }

    @Test
    void send() throws MessagingException {
        final ImmutableEmail request = ImmutableEmail.builder()
                .template("otp")
                .putParameters("otp", 148366)
                .to("receipient@test.com")
                .build();

        javaMailProvider.send(request);

        final List<MimeMessage> unread = messagesVisitor.getUnread();

        assertThat(unread).hasSize(1);

        final MimeMessage receivedMessage = unread.get(0);

        assertThat(receivedMessage.getSubject()).isEqualTo("OTP email");
        assertThat(GreenMailUtil.getBody(receivedMessage)).isEqualTo("OTP is 148366");
        assertThat(receivedMessage.getAllRecipients()).hasSize(1);
        assertThat(((InternetAddress)(receivedMessage.getAllRecipients()[0])).getAddress())
                .isEqualTo(request.getTo());
    }

    @Test
    void sendSsl() throws MessagingException {
        final ImmutableEmail request = ImmutableEmail.builder()
                .template("otp")
                .putParameters("otp", 148366)
                .to("receipient@test.com")
                .build();

        secureJavaMailProvider.send(request);

        final List<MimeMessage> unread = messagesVisitor.getUnread();

        assertThat(unread).hasSize(1);

        final MimeMessage receivedMessage = unread.get(0);

        assertThat(receivedMessage.getSubject()).isEqualTo("OTP email");
        assertThat(GreenMailUtil.getBody(receivedMessage)).isEqualTo("OTP is 148366");
        assertThat(receivedMessage.getAllRecipients()).hasSize(1);
        assertThat(((InternetAddress)(receivedMessage.getAllRecipients()[0])).getAddress())
                .isEqualTo(request.getTo());
    }

    @Test
    void sendSslWithWrongCredentials() throws MessagingException {
        final ImmutableEmail request = ImmutableEmail.builder()
                .template("otp")
                .putParameters("otp", 148366)
                .to("receipient@test.com")
                .build();

        wrongCredentialsJavaMailProvider.send(request);

        final List<MimeMessage> unread = messagesVisitor.getUnread();

        assertThat(unread).isEmpty();
    }
}