package com.ricaragas.paymybuddy.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@Configuration
public class LocaleConfig {

    @Bean
    public LocaleResolver localeResolver() {
        var localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(Locale.FRANCE);
        // Note: This is used by Thymeleaf to resolve currencies in html templates
        return localeResolver;
    }
}
