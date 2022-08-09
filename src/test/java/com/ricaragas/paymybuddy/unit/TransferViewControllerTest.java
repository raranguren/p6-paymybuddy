package com.ricaragas.paymybuddy.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@ExtendWith(SpringExtension.class)
public class TransferViewControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    public void when_get_then_success() throws Exception {
        mockMvc.perform(get("/transfer"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("transfer"))
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("contacts"))
                .andExpect(model().attributeExists("transfers"));
    }

}
