package com.nexblocks.authguard.external.email.config;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.Map;

@Value.Immutable
@Value.Style(
        validationMethod = Value.Style.ValidationMethod.NONE,
        jdkOnly = true
)
@JsonDeserialize(as = ImmutableSendGridConfig.class)
public interface SendGridConfig {
    String getApiKeyVariable();

    Map<String, String> getEmailTemplates();
}
