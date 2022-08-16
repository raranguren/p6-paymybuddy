package com.ricaragas.paymybuddy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MockBankController {

    @GetMapping("/mock-bank")
    public ModelAndView getMockBankPage(String ref) {
        var model = new ModelMap();
        model.addAttribute("ref", ref);
        return new ModelAndView("mock-bank", model);
    }

}
