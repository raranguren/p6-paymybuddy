package com.ricaragas.paymybuddy.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    public static final String URL_LOGIN = "/login";
    public static final String URL_SIGNUP = "/signup";
    public static final String URL_HOME = "/home";
    public static final String URL_TRANSFER = "/transfer";
    public static final String URL_NEW_CONNECTION = "/new-connection";
    public static final String URL_NEW_CONNECTION_SUCCESS = "/transfer?connection";
    public static final String URL_NEW_CONNECTION_ERROR_NOT_FOUND = "/new-connection?error";
    public static final String URL_NEW_CONNECTION_ERROR_DUPLICATED = "/new-connection?duplicated";
    public static final String URL_NEW_CONNECTION_ERROR_ADDED_SELF = "/new-connection?self";
    public static final String URL_PAY = "/pay";
    public static final String URL_PAY_SUCCESS = "/transfer?paid";
    public static final String URL_PROFILE = "/profile";
    public static final String URL_ADD_BALANCE = "/add-balance";
    public static final String URL_ADD_BALANCE_CHECKOUT = "/add-balance-checkout";
    public static final String URL_CALLBACK_FROM_BANK = "/add-balance-verify";
    public static final String URL_ADD_BALANCE_SUCCESS = "/pay?balanceAdded";
    public static final String URL_ADD_BALANCE_FAILED = "/add-balance?failed";
    public static final String URL_WITHDRAW = "/withdraw";
    public static final String URL_WITHDRAW_SUCCESS = "/profile?withdrew";
    public static final String URL_CONTACT = "/contact";

    // WebMvcConfigurerAdapter deprecated in Spring 5
    // upgrade guide: https://www.baeldung.com/web-mvc-configurer-adapter-deprecated

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController(URL_LOGIN).setViewName("login");
        registry.addRedirectViewController("/",URL_HOME);
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

}
