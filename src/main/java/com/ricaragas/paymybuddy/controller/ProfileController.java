package com.ricaragas.paymybuddy.controller;

import com.ricaragas.paymybuddy.service.WalletService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import static com.ricaragas.paymybuddy.configuration.WebConfig.URL_PROFILE;

@Controller
public class ProfileController {

    private final WalletService walletService;
    public ProfileController(WalletService walletService) {
        this.walletService = walletService;
    }


    // Profile page showing the current balance and two buttons to increase/withdraw

    @GetMapping(URL_PROFILE)
    public ModelAndView getProfile(ModelMap model) {
        var viewName = "profile";
        model.addAttribute("balance", walletService.getBalanceInEuros());
        return new ModelAndView(viewName, model);
    }
}
