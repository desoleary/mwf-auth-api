package com.mwf.auth_api.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mwf.auth_api.model.User;
import com.mwf.auth_api.payload.SignUpStepOneRequest;
import com.mwf.auth_api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = {"classpath:application-test.properties"})
public class SignupStepOneIntegrationTests {
    String name = "joe";
    String email = "joe@email.com";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Test
    void itShouldCreateInitialUser() throws Exception {
        SignUpStepOneRequest request = new SignUpStepOneRequest(name, email);
        mockMvc.perform(post("/api/auth/signup_step_one")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        User user = userRepository.findByEmail(email).get();
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getName()).isEqualTo(name);
    }

    @Test
    void itHandlesEmailAlreadyExists() throws Exception {
        User user = new User(name, email);
        userRepository.save(user);

        SignUpStepOneRequest request = new SignUpStepOneRequest(name, email);
        MvcResult result = mockMvc.perform(post("/api/auth/signup_step_one")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()).andReturn();

        String content = result.getResponse().getContentAsString();
        assertThat(content).contains("Email Address already in use!");
    }
}
