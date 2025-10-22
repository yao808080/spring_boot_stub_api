package com.example.stubapi.rest;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class RestStubControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void returnsConfiguredStubForGetRequest() throws Exception {
        mockMvc.perform(get("/stub/rest/customers/123"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Taro Stub")));
    }

    @Test
    void returnsConfiguredStubForPostRequest() throws Exception {
        mockMvc.perform(post("/stub/rest/orders"))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/orders/98765"));
    }

    @Test
    void returnsNotFoundForUnknownStub() throws Exception {
        mockMvc.perform(get("/stub/rest/unknown"))
            .andExpect(status().isNotFound())
            .andExpect(content().string(containsString("No stub mapping")));
    }
}
