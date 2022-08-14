package com.ricaragas.paymybuddy.controller;

import com.ricaragas.paymybuddy.service.WalletService;
import com.ricaragas.paymybuddy.service.exceptions.*;
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
    public static final String URL_PAY = "/pay";
    public static final String URL_PAY_SUCCESS = "/transfer?paid";
    public static final String URL_ADD_BALANCE = "/add-balance";

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
        return new ModelAndView("new-connection");
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

    @PostMapping(URL_PAY)
    public ModelAndView postPay(Long to, Double amount, String description) {
        var url = URL_PAY;
        try {
            walletService.pay(to, description, amount);
            url = URL_PAY_SUCCESS;
        } catch (NotFound e) {
            url = URL_PAY;
        } catch (TextTooShort e) {
            url = URL_PAY + "?to=" + to + "&amount=" + amount;
        } catch (NotEnoughBalance e) {
            var balanceNeeded = amount - walletService.getWalletForAuthenticatedUser().getBalanceInEuros();
            url = URL_PAY + "?to=" + to + "&amount=" + amount + "&description=" + description
                    + "&balance=" + balanceNeeded;
        } catch (InvalidAmount e) {
            url = URL_PAY + "?to=" + to + "&description=" + description;
        }
        return new ModelAndView("redirect:" + url);
    }

    @GetMapping(URL_PAY)
    public ModelAndView getPayPage() {
        var viewName = "pay";
        var model = new HashMap<String, Object>();
        var connections = walletService.getWalletForAuthenticatedUser().getConnections();
        model.put("connections", connections);
        return new ModelAndView(viewName, model);
    }

    @GetMapping(URL_ADD_BALANCE)
    public ModelAndView getAddBalance() {
        var viewName = "add-balance";
        return new ModelAndView(viewName);
    }

}
