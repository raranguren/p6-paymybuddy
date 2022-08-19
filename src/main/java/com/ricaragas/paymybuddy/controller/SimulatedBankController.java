package com.ricaragas.paymybuddy.controller;

import com.ricaragas.paymybuddy.service.BillingServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SimulatedBankController {

    public static final String URL = "/simulated-bank";

    @Autowired
    BillingServiceImpl mockBillingService;

    @GetMapping(URL)
    public ModelAndView getMockBankPage(String simulatedPayment, String ref) {
        var model = new ModelMap();
        model.addAttribute("ref", ref);
        mockBillingService.finishSimulatedTransaction(simulatedPayment, true);
        return new ModelAndView("simulated-bank", model);
    }

}
