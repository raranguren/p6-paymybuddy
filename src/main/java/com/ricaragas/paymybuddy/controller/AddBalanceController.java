package com.ricaragas.paymybuddy.controller;

import com.ricaragas.paymybuddy.exceptions.InvalidAmountException;
import com.ricaragas.paymybuddy.service.WalletService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import static com.ricaragas.paymybuddy.configuration.WebConfig.*;

@Controller
public class AddBalanceController {

    private final WalletService walletService;
    AddBalanceController(WalletService walletService) {
        this.walletService = walletService;
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
                url = walletService.getUrlAndStartAddingMoney(invoice);
            }
        } catch (InvalidAmountException e) {
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
    //  - will not start bank transfer until the button with name "confirmation" is pressed

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
