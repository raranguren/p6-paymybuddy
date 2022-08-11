package com.ricaragas.paymybuddy.unit;

import com.ricaragas.paymybuddy.controller.WalletController;
import com.ricaragas.paymybuddy.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = WalletController.class)
public class WalletControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    WalletService walletService;

    @Test
    @WithMockUser
    public void when_get_then_success() throws Exception {
        mockMvc.perform(get("/transfer"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("transfer"))
                .andExpect(model().attributeExists("contacts"))
                .andExpect(model().attributeExists("transfers"));
    }

}
