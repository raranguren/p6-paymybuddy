package com.ricaragas.paymybuddy.unit;

import com.ricaragas.paymybuddy.controller.WebController;
import com.ricaragas.paymybuddy.model.Wallet;
import com.ricaragas.paymybuddy.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.ricaragas.paymybuddy.controller.WebController.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = WebController.class)
@AutoConfigureMockMvc(addFilters = false)
public class WebControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    WalletService walletService;

    @ParameterizedTest
    @ValueSource(strings = {URL_PAY, URL_TRANSFER, URL_ADD_BALANCE, URL_ADD_BALANCE_CHECKOUT, URL_NEW_CONNECTION})
    public void thymeleaf_compiles_the_views(String url) throws Exception {
        mockMvc.perform(get(url))
                .andExpect(status().is2xxSuccessful());
        // simply testing that Thymeleaf compiles the views
    }

    @ParameterizedTest
    @ValueSource(strings = {URL_PAY, URL_TRANSFER, URL_ADD_BALANCE, URL_ADD_BALANCE_CHECKOUT, URL_NEW_CONNECTION})
    public void all_post_requests_redirect() throws Exception {
        mockMvc.perform(post(URL_PAY)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("to", "1")
                        .param("amount", "10.20")
                        .param("description", "Any reason"))
                .andExpect(status().is3xxRedirection());
    }

}
