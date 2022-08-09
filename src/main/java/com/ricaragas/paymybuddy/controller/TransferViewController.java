package com.ricaragas.paymybuddy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;

@Controller
public class TransferViewController {

    @GetMapping("/transfer")
    public ModelAndView getTransferPage() {
        var viewName = "transfer";
        var model = new HashMap<String, Object>();

        model.put("contacts", null);
        model.put("transfers", null);

        return new ModelAndView(viewName, model);
    }

}
