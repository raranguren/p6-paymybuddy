package com.ricaragas.paymybuddy.controller;

import com.ricaragas.paymybuddy.exceptions.*;
import com.ricaragas.paymybuddy.service.WalletService;
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
    public static final String URL_PROFILE = "/profile";
    public static final String URL_ADD_BALANCE = "/add-balance";
    public static final String URL_ADD_BALANCE_CHECKOUT = "/add-balance-checkout";
    public static final String URL_CALLBACK_FROM_BANK = "/add-balance-verify";
    public static final String URL_ADD_BALANCE_SUCCESS = "/pay?balanceAdded";
    public static final String URL_ADD_BALANCE_FAILED = "/add-balance?failed";
    public static final String URL_WITHDRAW = "/withdraw";
    public static final String URL_WITHDRAW_SUCCESS = "/profile?withdrew";

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
        model.put("transfers", walletService.getSentTransfersPage(1, 5));
        model.put("pages", walletService.getSentTransfersPageCount()); // TODO pages in html

        return new ModelAndView(viewName, model);
    }

    // When using "Get connection" button, show a form asking for email
    // and name. The form remembers what was posted in case of error.

    @GetMapping(URL_NEW_CONNECTION)
    public ModelAndView getNewConnectionForm() {
        return new ModelAndView("new-connection");
    }

    @PostMapping(URL_NEW_CONNECTION)
    public RedirectView postNewConnection(String name, String email, RedirectAttributes redirectAttributes) {
        var url = URL_NEW_CONNECTION_SUCCESS;
        try {
            walletService.addConnection(name, email);
        } catch (IsSameUserException e) {
            url = URL_NEW_CONNECTION_ERROR_ADDED_SELF;
        } catch (NotFoundException e) {
            url = URL_NEW_CONNECTION_ERROR_NOT_FOUND;
        } catch (IsDuplicatedException e) {
            url = URL_NEW_CONNECTION_ERROR_DUPLICATED;
        } catch (TextTooShortException e) {
            url = URL_NEW_CONNECTION;
        }
        redirectAttributes.addFlashAttribute("name", name);
        redirectAttributes.addFlashAttribute("email", email);
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
        } catch (NotFoundException e) {
            model.remove("to");
        } catch (TextTooShortException e) {
            model.remove("description");
        } catch (NotEnoughBalanceException e) {
            var balanceNeeded = amount - walletService.getBalanceInEuros();
            redirectAttributes.addFlashAttribute("balanceNeeded", balanceNeeded);
        } catch (InvalidAmountException e) {
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

    // When withdrawing from the user balance to the bank
    // Confirmation page that shows a detailed invoice with the fee
    // A cancel button sends them back
    //  - alerts shown with model values balanceHasChanged and notEnoughBalance
    //  - will not start bank transfer until the button with name "confirmation" is pressed

    @GetMapping(URL_WITHDRAW)
    public ModelAndView getWithdraw(ModelMap model) {
        var viewName = "withdraw-balance-checkout";
        try {
            var invoice = walletService.getInvoiceToWithdrawAll();
            model.addAttribute("balanceInEuros", - invoice.getTransferInEuros());
            model.addAttribute("balanceConfirmationInCents", - invoice.getTransferInCents());
            model.addAttribute("fee", invoice.getFeeInEuros());
            model.addAttribute("vat", invoice.getVatInEuros());
            model.addAttribute("total", - invoice.getTotalInEuros());
        } catch (NotEnoughBalanceException e) {
            model.addAttribute("notEnoughBalance", true);
        }
        return new ModelAndView(viewName, model);
    }

    @PostMapping(URL_WITHDRAW)
    public RedirectView postWithdraw(String confirmation, Long balanceConfirmationInCents,
                                     RedirectAttributes redirectAttributes) {
        var url = URL_WITHDRAW;
        if (confirmation != null) try {
            var invoice = walletService.getInvoiceToWithdrawAll();
            walletService.startBalanceWithdrawal(invoice, balanceConfirmationInCents);
            url = URL_WITHDRAW_SUCCESS;
        } catch (NotEnoughBalanceException e) {
            redirectAttributes.addFlashAttribute("notEnoughBalance", true);
        } catch (InvalidAmountException e) {
            redirectAttributes.addFlashAttribute("balanceHasChanged", true);
        }
        return new RedirectView(url);
    }

    // Profile page showing the current balance and two buttons to increase/withdraw

    @GetMapping(URL_PROFILE)
    public ModelAndView getProfile(ModelMap model) {
        var viewName = "profile";
        model.addAttribute("balance", walletService.getBalanceInEuros());
        return new ModelAndView(viewName, model);
    }

}
