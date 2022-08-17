package com.ricaragas.paymybuddy.controller;

import com.ricaragas.paymybuddy.service.MockBillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MockBankController {

    @Autowired
    MockBillingService mockBillingService;

    @GetMapping("/mock-bank")
    public ModelAndView getMockBankPage(String mockPayment, String ref) {
        var model = new ModelMap();
        model.addAttribute("ref", ref);
        mockBillingService.finishTransaction(mockPayment, true);
        return new ModelAndView("mock-bank", model);
    }

}
