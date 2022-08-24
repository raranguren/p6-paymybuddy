package com.ricaragas.paymybuddy.controller;

import com.ricaragas.paymybuddy.service.ConnectionService;
import com.ricaragas.paymybuddy.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import static com.ricaragas.paymybuddy.configuration.WebConfig.*;

import java.security.Principal;
import java.util.HashMap;

@Controller
public class TransferController {

    @Autowired
    TransferService transferService;

    @Autowired
    ConnectionService connectionService;

    // Main transfer page with Pay form, Add connection button, and a list of transfers

    @GetMapping(URL_TRANSFER)
    public ModelAndView showTransferSection(Principal principal) {
        var viewName = "transfer";
        var model = new HashMap<String, Object>();
        var activeUserEmail = principal.getName();

        model.put("connections", connectionService.getAvailableConnections(activeUserEmail));
        model.put("transfers", transferService.getSentTransfers(activeUserEmail));
        return new ModelAndView(viewName, model);
    }

}
