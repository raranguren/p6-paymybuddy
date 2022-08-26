package com.ricaragas.paymybuddy.integration;

import com.ricaragas.paymybuddy.service.UserService;
import com.ricaragas.paymybuddy.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.ricaragas.paymybuddy.configuration.WebConfig.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ControllersIT {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserService userService;
    @Autowired
    WalletService walletService;

    final String emailA = "adan@mail.fr";
    final String emailB = "bea@mail.fr";
    final String emailC = "carl@mail.fr";
    final String passwordRaw = "123.ITest";

    @BeforeEach
    void before_each() throws Exception {
        userService.createUser(emailA, passwordRaw);
        userService.createUser(emailB, passwordRaw);
        walletService.doBalanceUpdate(walletService.findByEmail(emailA).orElseThrow(), 1000);
    }

    @Test
    void user_C_can_sign_up() throws Exception {
        mockMvc.perform(post(URL_SIGNUP)
                .param("email", emailC)
                .param("password", passwordRaw)
                .with(csrf()));
        mockMvc.perform(post(URL_LOGIN)
                .param("email", emailC)
                .param("password", passwordRaw)
                .with(csrf()))
                .andExpect(redirectedUrl(URL_HOME));
    }

    @Test
    @WithMockUser(username = emailA)
    void user_A_can_connect_and_send_money_to_user_B() throws Exception {
        mockMvc.perform(get(URL_NEW_CONNECTION))
                .andExpect(status().isOk());
        mockMvc.perform(post(URL_NEW_CONNECTION)
                .param("email", emailB)
                .param("name", "friend"));
        mockMvc.perform(get(URL_PAY))
                .andExpect(status().isOk());
        mockMvc.perform(post(URL_PAY)
                .param("to", "1")
                .param("amount", "5.20")
                .param("description", "test pay money"));
        mockMvc.perform(get(URL_TRANSFER))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = emailA)
    void user_A_can_send_money_to_bank() throws Exception {
        mockMvc.perform(get(URL_WITHDRAW))
                        .andExpect(status().isOk());
        mockMvc.perform(post(URL_WITHDRAW)
                .param("confirmation", "accept")
                .param("balanceConfirmationInCents", "1000"));
    }

    @Test
    @WithMockUser(username = emailA)
    void user_A_can_add_money_to_balance() throws Exception {
        mockMvc.perform(get(URL_ADD_BALANCE))
                .andExpect(status().isOk());
        mockMvc.perform(post(URL_ADD_BALANCE_CHECKOUT)
                .param("confirmation", "accept")
                .param("amountToAdd", "23.45"));
        mockMvc.perform(get(URL_PROFILE))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void user_can_see_home_and_contact_page() throws Exception {
        mockMvc.perform(get(URL_HOME))
                .andExpect(status().isOk());
        mockMvc.perform(get(URL_CONTACT_US))
                .andExpect(status().isOk());
    }
}
