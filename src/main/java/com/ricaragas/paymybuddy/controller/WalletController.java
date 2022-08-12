package com.ricaragas.paymybuddy.controller;

import com.ricaragas.paymybuddy.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;

@Controller
public class WalletController {

    @Autowired
    WalletService walletService;

    @GetMapping("/transfer")
    public ModelAndView getTransferPage() {
        var viewName = "transfer";
        var model = new HashMap<String, Object>();

        var wallet = walletService.getWalletForAuthenticatedUser();
        if (wallet.isEmpty()) return new ModelAndView("redirect:/error");

        model.put("connections", wallet.get().getConnections());
        model.put("transfers", wallet.get().getSentTransfers());

        return new ModelAndView(viewName, model);
    }

    @GetMapping("/new-connection")
    public ModelAndView getNewConnectionPage() {
        var viewName = "new-connection";
        var model = new HashMap<String, Object>();

        return new ModelAndView(viewName, model);
    }

    @PostMapping("/new-connection")
    public ModelAndView postNewConnection() {
        return new ModelAndView();
    }


}
