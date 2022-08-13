package com.ricaragas.paymybuddy.controller;

import com.ricaragas.paymybuddy.service.WalletService;
import com.ricaragas.paymybuddy.service.exceptions.IsCurrentUser;
import com.ricaragas.paymybuddy.service.exceptions.IsDuplicated;
import com.ricaragas.paymybuddy.service.exceptions.NotFound;
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
    
    public static final String URL_TRANSFER = "/transfer";
    public static final String URL_NEW_CONNECTION = "/new-connection";
    public static final String URL_NEW_CONNECTION_SUCCESS = "/transfer?connection";
    public static final String URL_NEW_CONNECTION_ERROR_NOT_FOUND = "/new-connection?error";
    public static final String URL_NEW_CONNECTION_ERROR_DUPLICATED = "/new-connection?duplicated";
    public static final String URL_NEW_CONNECTION_ERROR_ADDED_SELF = "/new-connection?self";

    @GetMapping(URL_TRANSFER)
    public ModelAndView getTransferPage() {
        var viewName = "transfer";
        var model = new HashMap<String, Object>();

        var wallet = walletService.getWalletForAuthenticatedUser();

        model.put("connections", wallet.getConnections());
        model.put("transfers", wallet.getSentTransfers());

        return new ModelAndView(viewName, model);
    }

    @GetMapping(URL_NEW_CONNECTION)
    public ModelAndView getNewConnectionPage() {
        var viewName = "new-connection";
        var model = new HashMap<String, Object>();

        return new ModelAndView(viewName, model);
    }

    @PostMapping(URL_NEW_CONNECTION)
    public ModelAndView postNewConnection(String email) {
        var url = URL_NEW_CONNECTION_SUCCESS;
        try {
            walletService.addConnection(email);
        } catch (IsCurrentUser e) {
            url = URL_NEW_CONNECTION_ERROR_ADDED_SELF;
        } catch (NotFound e) {
            url = URL_NEW_CONNECTION_ERROR_NOT_FOUND;
        } catch (IsDuplicated e) {
            url = URL_NEW_CONNECTION_ERROR_DUPLICATED;
        }
        return new ModelAndView("redirect:" + url);
    }

}
