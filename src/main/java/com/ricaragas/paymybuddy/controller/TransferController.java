package com.ricaragas.paymybuddy.controller;

import com.ricaragas.paymybuddy.service.ConnectionService;
import com.ricaragas.paymybuddy.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import static com.ricaragas.paymybuddy.configuration.WebConfig.*;

import java.util.HashMap;

@Controller
public class TransferController {

    @Autowired
    TransferService transferService;

    @Autowired
    ConnectionService connectionService;

    // Main transfer page with Pay form, Add connection button, and a list of transfers

    @GetMapping(URL_TRANSFER)
    public ModelAndView showTransferSection() {
        var viewName = "transfer";
        var model = new HashMap<String, Object>();

        model.put("connections", connectionService.getAvailableConnections());
        model.put("transfers", transferService.getSentTransfers());
        return new ModelAndView(viewName, model);
    }

}
