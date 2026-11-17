package com.richard.portfolio;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PortfolioApplicationTests {

    @Autowired
    private MockMvc mvc;

    @Test
    void homePageLoads() throws Exception {
        mvc.perform(get("/"))
           .andExpect(status().isOk())
           .andExpect(content().string(containsString("Richard")))
           .andExpect(content().string(containsString("DevOps Engineer")))
           .andExpect(content().string(containsString("2 years")));
    }

    @Test
    void infoEndpointReturnsPod() throws Exception {
        mvc.perform(get("/api/info"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.pod").exists())
           .andExpect(jsonPath("$.app").value("richard-portfolio"));
    }

    @Test
    void healthIsUp() throws Exception {
        mvc.perform(get("/actuator/health"))
           .andExpect(status().isOk());
    }
}

