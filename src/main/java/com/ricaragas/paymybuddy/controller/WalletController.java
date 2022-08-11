package com.ricaragas.paymybuddy.controller;

import com.ricaragas.paymybuddy.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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
        if (wallet.isEmpty()) {
            throw new RuntimeException("No wallet found for logged in user");
            // TODO handle gracefully. Maybe just logout
        }
        System.out.println(wallet.get().getContacts().size());
        System.out.println(wallet.get().getSentTransfers().size());
        model.put("contacts", wallet.get().getContacts());
        model.put("transfers", wallet.get().getSentTransfers());

        return new ModelAndView(viewName, model);
    }

}
