package com.nexblocks.authguard.email.jmail;

import io.vavr.control.Try;

import java.util.Map;

public interface TemplateResolver {
    Try<String> resolve(String template, Map<String, Object> parameters);
}
