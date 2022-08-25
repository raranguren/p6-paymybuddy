package com.ricaragas.paymybuddy.controller;

import com.ricaragas.paymybuddy.service.ConnectionService;
import com.ricaragas.paymybuddy.service.TransferService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import static com.ricaragas.paymybuddy.configuration.WebConfig.*;

import java.util.HashMap;

@Controller
public class TransferController {

    private final TransferService transferService;
    private final ConnectionService connectionService;
    public TransferController(TransferService transferService, ConnectionService connectionService) {
        this.transferService = transferService;
        this.connectionService = connectionService;
    }

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
