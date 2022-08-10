package com.ricaragas.paymybuddy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TransferViewController {

    @GetMapping("/transfer")
    public ModelAndView getTransferPage() {
        var viewName = "transfer";
        var model = new HashMap<String, Object>();

        var contacts = new ArrayList<>(List.of("Contact1", "Contact2"));

        var transfers = new ArrayList<HashMap<String,String>>();
        transfers.add(new HashMap<>(Map.of("Col1","Value1","Col2","Value2","Col3","Value3")));

        model.put("contacts", contacts);
        model.put("transfers", transfers);

        return new ModelAndView(viewName, model);
    }

}
