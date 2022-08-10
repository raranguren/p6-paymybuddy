package com.ricaragas.paymybuddy.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
// @EnableWebSecurity deprecated in Spring Boot 2.7
public class SpringSecurityConfig {
    // extends WebSecurityConfigurerAdapter deprecated in Spring Boot 2.7
    // Instead, using @Bean filterChain and @Bean webSecurityCustomizer
    // Upgrade guide: https://www.codejava.net/frameworks/spring-boot/fix-websecurityconfigureradapter-deprecated

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeRequests()
                .antMatchers("/styles/*", "/icons/*").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().usernameParameter("email").defaultSuccessUrl("/")
                .loginPage("/login").permitAll();
        return httpSecurity.build();
    }

    @Bean
    PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
