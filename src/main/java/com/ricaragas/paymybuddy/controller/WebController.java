package com.ricaragas.paymybuddy.controller;

import com.ricaragas.paymybuddy.service.WalletService;
import com.ricaragas.paymybuddy.service.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;

@Controller
@SessionAttributes("state")
public class WebController {

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
    public static final String URL_ADD_BALANCE_CHECKOUT = "/add-balance-checkout";

    @ModelAttribute("state")
    public ModelMap state() {
        return new ModelMap();
    }

    @GetMapping(URL_TRANSFER)
    public ModelAndView getTransferPage() {
        var viewName = "transfer";
        var model = new HashMap<String, Object>();

        model.put("connections", walletService.getConnectionOptions());
        model.put("transfers", walletService.getSentTransfers(1));

        return new ModelAndView(viewName, model);
    }

    @GetMapping(URL_NEW_CONNECTION)
    public ModelAndView getNewConnectionPage() {
        return new ModelAndView("new-connection");
    }

    @PostMapping(URL_NEW_CONNECTION)
    public RedirectView postNewConnection(String email) {
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
        return new RedirectView(url);
    }

    @PostMapping(URL_PAY)
    public RedirectView postPay(Long to, Double amount, String description,
                                RedirectAttributes redirectAttributes) {
        var url = URL_PAY;
        redirectAttributes.addAttribute("to", to);
        redirectAttributes.addAttribute("amount", amount);
        redirectAttributes.addFlashAttribute("description", description);
        try {
            walletService.pay(to, description, amount);
            url = URL_PAY_SUCCESS;
        } catch (NotFound e) {
            redirectAttributes.addAttribute("to", null);
        } catch (TextTooShort e) {
            redirectAttributes.addAttribute("description", null);
        } catch (NotEnoughBalance e) {
            var balanceNeeded = amount - walletService.getBalanceInEuros();
            redirectAttributes.addFlashAttribute("balanceNeeded", balanceNeeded);
        } catch (InvalidAmount e) {
            redirectAttributes.addAttribute("amount", null);
        }
        redirectAttributes.addFlashAttribute("test", "testing");
        return new RedirectView(url);
    }

    @GetMapping(URL_PAY)
    public ModelAndView getPayPage(ModelMap model) {
        var viewName = "pay";
        model.put("connections", walletService.getConnectionOptions());
        return new ModelAndView(viewName, model);
    }

    @PostMapping(URL_ADD_BALANCE)
    public RedirectView postAddBalance(Long to, Double amountToPay, String description, Double balanceNeeded,
                                       Double amountToAdd, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("to", to);
        redirectAttributes.addFlashAttribute("amountToPay", amountToPay);
        redirectAttributes.addFlashAttribute("description", description);
        redirectAttributes.addFlashAttribute("balanceNeeded", balanceNeeded);
        redirectAttributes.addFlashAttribute("amountToAdd", amountToAdd);
        return new RedirectView(URL_ADD_BALANCE);
    }

    @GetMapping(URL_ADD_BALANCE)
    public ModelAndView getAddBalance(String transactionId, ModelMap model) {
        var viewName = "add-balance";
        if (transactionId != null) {
            if (walletService.isTransactionSuccessful(transactionId)) {
                model.addAttribute("balance-updated");
            } else {
                model.addAttribute("payment-failed");
            }
        }
        return new ModelAndView(viewName, model);
    }

    @PostMapping(URL_ADD_BALANCE_CHECKOUT)
    public RedirectView putCheckout(Long to, Double amountToPay, String description,
                                    String confirmation,
                                    double amountToAdd, RedirectAttributes redirectAttributes) {
        var url = URL_ADD_BALANCE_CHECKOUT;
        redirectAttributes.addFlashAttribute("to", to);
        redirectAttributes.addFlashAttribute("amountToPay", amountToPay);
        redirectAttributes.addFlashAttribute("description", description);
        redirectAttributes.addFlashAttribute("amountToAdd", amountToAdd);
        try {
            var invoice = walletService.getInvoiceToAddAmount(amountToAdd);
            redirectAttributes.addFlashAttribute("fee", invoice.getFeeInEuros());
            redirectAttributes.addFlashAttribute("vat", invoice.getVatInEuros());
            redirectAttributes.addFlashAttribute("total", invoice.getTotalInEuros());
            if (confirmation != null && amountToAdd > 0.0) {
                url = walletService.getUrlToAddMoney(invoice);
            }
        } catch (InvalidAmount e) {
            url = URL_ADD_BALANCE;
        }
        return new RedirectView(url);
    }

    @GetMapping(URL_ADD_BALANCE_CHECKOUT)
    public ModelAndView getCheckout(ModelMap model) {
        var viewName = "add-balance-checkout";
        return new ModelAndView(viewName, model);
    }

}
