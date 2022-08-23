package com.ricaragas.paymybuddy.controller;

import com.ricaragas.paymybuddy.exceptions.InvalidAmountException;
import com.ricaragas.paymybuddy.exceptions.NotEnoughBalanceException;
import com.ricaragas.paymybuddy.exceptions.NotFoundException;
import com.ricaragas.paymybuddy.exceptions.TextTooShortException;
import com.ricaragas.paymybuddy.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import static com.ricaragas.paymybuddy.configuration.WebConfig.*;

@Controller
public class PayController {

    @Autowired
    WalletService walletService;

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

}
