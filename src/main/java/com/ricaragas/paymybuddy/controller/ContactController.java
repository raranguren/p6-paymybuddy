package com.ricaragas.paymybuddy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import static com.ricaragas.paymybuddy.configuration.WebConfig.*;

@Controller
public class ContactController {

    @GetMapping(URL_CONTACT)
    public ModelAndView main() {
        return new ModelAndView("contact");
    }

}
