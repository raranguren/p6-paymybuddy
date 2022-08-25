package com.ricaragas.paymybuddy.controller;

import com.ricaragas.paymybuddy.exceptions.InvalidAmountException;
import com.ricaragas.paymybuddy.exceptions.NotEnoughBalanceException;
import com.ricaragas.paymybuddy.exceptions.NotFoundException;
import com.ricaragas.paymybuddy.exceptions.TextTooShortException;
import com.ricaragas.paymybuddy.service.ConnectionService;
import com.ricaragas.paymybuddy.service.TransferService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;

import static com.ricaragas.paymybuddy.configuration.WebConfig.*;

@Controller
@SessionAttributes("pay-form")
public class PayController {

    private final TransferService transferService;
    private final ConnectionService connectionService;
    public PayController(TransferService transferService, ConnectionService connectionService) {
        this.transferService = transferService;
        this.connectionService = connectionService;
    }


    // When user comes back from adding balance,
    // the form is filled with their previous target/amount/description

    @ModelAttribute("pay-form")
    public ModelMap persistPayFormModelWithSession() {
        return new ModelMap();
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
                                Principal principal,
                                @ModelAttribute("pay-form") ModelMap model,
                                RedirectAttributes redirectAttributes) {
        var url = URL_PAY;
        model.addAttribute("to", to);
        model.addAttribute("amount", amount);
        model.addAttribute("description", description);
        try {
            transferService.createTransfer(to, description, amount);
            model.remove("to");
            model.remove("amount");
            model.remove("description");
            url = URL_PAY_SUCCESS;
        } catch (NotFoundException e) {
            model.remove("to");
        } catch (TextTooShortException e) {
            model.remove("description");
        } catch (NotEnoughBalanceException e) {
            var balanceNeeded = transferService.getBalanceNeededForTransfer(principal.getName(), amount);
            redirectAttributes.addFlashAttribute("balanceNeeded", balanceNeeded);
        } catch (InvalidAmountException e) {
            model.remove("amount");
        }
        return new RedirectView(url);
    }

    @GetMapping(URL_PAY)
    public ModelAndView getPayPage(@ModelAttribute("pay-form") ModelMap model) {
        var viewName = "pay";
        model.put("connections", connectionService.getAvailableConnections());
        return new ModelAndView(viewName, model);
    }

}
