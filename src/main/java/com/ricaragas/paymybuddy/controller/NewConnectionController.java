package com.ricaragas.paymybuddy.controller;

import com.ricaragas.paymybuddy.exceptions.IsDuplicatedException;
import com.ricaragas.paymybuddy.exceptions.IsSameUserException;
import com.ricaragas.paymybuddy.exceptions.NotFoundException;
import com.ricaragas.paymybuddy.exceptions.TextTooShortException;
import com.ricaragas.paymybuddy.service.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import static com.ricaragas.paymybuddy.configuration.WebConfig.*;

@Controller
public class NewConnectionController {

    @Autowired
    ConnectionService connectionService;

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
            connectionService.createConnection(email, name);
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

}
