package com.ricaragas.paymybuddy.controller;

import com.ricaragas.paymybuddy.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import static com.ricaragas.paymybuddy.configuration.WebConfig.*;

import java.util.HashMap;

@Controller
@SessionAttributes("pay-form")
public class TransferController {

    @Autowired
    WalletService walletService;

    @ModelAttribute("pay-form")
    public ModelMap persistPayFormModelWithSession() {
        return new ModelMap();
    }

    // Main transfer page with Pay form, Add connection button, and a list of transfers

    @GetMapping(URL_TRANSFER)
    public ModelAndView showTransferSection() {
        var viewName = "transfer";
        var model = new HashMap<String, Object>();

        model.put("connections", walletService.getConnectionOptions());
        model.put("transfers", walletService.getSentTransfers());
        return new ModelAndView(viewName, model);
    }

}
