package com.ricaragas.paymybuddy.controller;

import com.ricaragas.paymybuddy.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class WalletController {

    @Autowired
    WalletService walletService;

    @GetMapping("/transfer")
    public ModelAndView getTransferPage() {
        var wallet = walletService.getWalletForAuthenticatedUser();

        var viewName = "transfer";
        var model = new HashMap<String, Object>();
        var contactNames = new ArrayList<>(List.of("Contact1", "Contact2"));

        var transfersMockTable = new ArrayList<HashMap<String,String>>();
        transfersMockTable.add(new HashMap<>(Map.of("Col1","Value1","Col2","Value2","Col3","Value3")));

        model.put("contacts", contactNames);
        model.put("transfers", transfersMockTable);

        return new ModelAndView(viewName, model);
    }

}
