package com.ricaragas.paymybuddy.controller;

import com.ricaragas.paymybuddy.exceptions.InvalidAmountException;
import com.ricaragas.paymybuddy.exceptions.NotEnoughBalanceException;
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
public class WithdrawController {

    private final WalletService walletService;
    public WithdrawController(WalletService walletService) {
        this.walletService = walletService;
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
    public RedirectView postWithdraw(String confirmation, Integer balanceConfirmationInCents,
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

}
