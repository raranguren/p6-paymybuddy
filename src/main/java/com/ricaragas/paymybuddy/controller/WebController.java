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
@SessionAttributes("pay-form")
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
    public static final String URL_CALLBACK_FROM_BANK = "/add-balance-verify";
    public static final String URL_ADD_BALANCE_SUCCESS = "/pay?balanceAdded";
    public static final String URL_ADD_BALANCE_FAILED = "/add-balance?failed";

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
        model.put("transfers", walletService.getSentTransfersPage(1));
        model.put("pages", walletService.getSentTransfersPageCount()); // TODO in html

        return new ModelAndView(viewName, model);
    }

    // When using "Get connection" button, show a form asking for email

    @GetMapping(URL_NEW_CONNECTION)
    public ModelAndView getNewConnectionForm() {
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

    // When the Pay button is pressed, the values are saved in the session
    //   - connection selected
    //   - amount to send
    //   - text description of the payment
    // Ask for a description if missing
    // Then try to send the payment.
    // If the user doesn't have that much money, offer to "Add balance"
    // This is the landing page after adding balance and remembers the values entered before.

    @PostMapping(URL_PAY)
    public RedirectView postPay(Long to, Double amount, String description,
                                @ModelAttribute("pay-form") ModelMap model,
                                RedirectAttributes redirectAttributes) {
        var url = URL_PAY;
        model.addAttribute("to", to);
        model.addAttribute("amount", amount);
        model.addAttribute("description", description);
        try {
            walletService.pay(to, description, amount);
            model.remove("to");
            model.remove("amount");
            model.remove("description");
            url = URL_PAY_SUCCESS;
        } catch (NotFound e) {
            model.remove("to");
        } catch (TextTooShort e) {
            model.remove("description");
        } catch (NotEnoughBalance e) {
            var balanceNeeded = amount - walletService.getBalanceInEuros();
            redirectAttributes.addFlashAttribute("balanceNeeded", balanceNeeded);
        } catch (InvalidAmount e) {
            model.remove("amount");
        }
        return new RedirectView(url);
    }

    @GetMapping(URL_PAY)
    public ModelAndView getPayPage(@ModelAttribute("pay-form") ModelMap model) {
        var viewName = "pay";
        model.put("connections", walletService.getConnectionOptions());
        return new ModelAndView(viewName, model);
    }

    // Form to add balance to the current user
    // If it comes from the Pay form, it defaults to the amount that was missing for the payment

    @PostMapping(URL_ADD_BALANCE)
    public RedirectView postAddBalance(Double balanceNeeded, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("balanceNeeded", balanceNeeded);
        return new RedirectView(URL_ADD_BALANCE);
    }

    @GetMapping(URL_ADD_BALANCE)
    public ModelAndView getAddBalance(ModelMap model) {
        var viewName = "add-balance";
        return new ModelAndView(viewName, model);
    }

    // Confirmation page that shows a detailed invoice
    // A button to accept redirects the user to their bank to complete the payment
    // A cancel button sends them back to the main page

    @PostMapping(URL_ADD_BALANCE_CHECKOUT)
    public RedirectView putCheckout(String confirmation, double amountToAdd,
                                    RedirectAttributes redirectAttributes) {
        var url = URL_ADD_BALANCE_CHECKOUT;
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

    // When returning from the bank, call the service
    //  - If balance updated, redirect to Pay form
    //  - If balance not updated, redirect to the "add balance" form to show an error

    @GetMapping(URL_CALLBACK_FROM_BANK)
    public RedirectView getVerify(String transactionId) {
        if (transactionId != null) {
            if (walletService.isTransactionSuccessful(transactionId)) {
                return new RedirectView(URL_ADD_BALANCE_SUCCESS);
            }
            return new RedirectView(URL_ADD_BALANCE_FAILED);
        }
        return new RedirectView(URL_TRANSFER);
    }

}
