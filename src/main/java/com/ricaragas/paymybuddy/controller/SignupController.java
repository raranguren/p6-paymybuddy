package com.ricaragas.paymybuddy.controller;

import com.ricaragas.paymybuddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import static com.ricaragas.paymybuddy.configuration.WebConfig.URL_LOGIN;
import static com.ricaragas.paymybuddy.configuration.WebConfig.URL_SIGNUP;

@Controller
public class SignupController {

    @Autowired
    UserService userService;

    @GetMapping(URL_SIGNUP)
    public ModelAndView page() {
        return new ModelAndView("signup");
    }

    @PostMapping(URL_SIGNUP)
    public RedirectView submit(String email, String password) {
        var url = URL_SIGNUP;
        try {
            userService.createUser(email, password);
            url = URL_LOGIN + "?created";
        } catch (IllegalArgumentException e) {
            url += "?error";
        }
        return new RedirectView(url);
    }

}
