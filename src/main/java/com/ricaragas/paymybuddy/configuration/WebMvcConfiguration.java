package com.ricaragas.paymybuddy.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    // WebMvcConfigurerAdapter deprecated in Spring 5
    // upgrade guide: https://www.baeldung.com/web-mvc-configurer-adapter-deprecated

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
        registry.addRedirectViewController("/","/transfer");
        registry.addViewController("/transfer").setViewName("transfer");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

}
