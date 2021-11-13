package com.itmo.microservices.shop.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itmo.microservices.shop.common.test.NoWebSecurityTestCase;
import com.itmo.microservices.shop.user.api.model.RegistrationRequest;
import com.itmo.microservices.shop.user.api.model.UserModel;
import com.itmo.microservices.shop.user.impl.service.DefaultUserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.isA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("dev")
@AutoConfigureMockMvc
public class UserControllerTest extends NoWebSecurityTestCase {
    private final UUID uuid = UUID.randomUUID();
    private final String username = "Test";
    private final String password = "qwerty1234";
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final static ObjectMapper mapper = new ObjectMapper();

    @MockBean
    private DefaultUserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenPostCreateUser_thenReturnUserModel() throws Exception {
        UserModel model = new UserModel(UUID.randomUUID(), username, password, false);
        Mockito.doReturn(model).when(userService).registerUser(isA(RegistrationRequest.class));

        RegistrationRequest request = new RegistrationRequest(username, password);

        final String expectedResponseContent = mapper.writeValueAsString(model);

        this.mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponseContent));

        Mockito.verify(userService).registerUser(Mockito.any(RegistrationRequest.class));
    }

    @Test
    void whenGetUserById_thenReturnUserModel() throws Exception {
        UserModel userModel = createDefaultUserModel();
        Mockito.when(userService.getUserByID(userModel.getUuid())).thenReturn(userModel);

        final String expectedResponseContent = mapper.writeValueAsString(userModel);

        this.mockMvc.perform(get("/users/" + userModel.getUuid()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(expectedResponseContent));

        Mockito.verify(userService).getUserByID(Mockito.any(UUID.class));
    }

    private UserModel createDefaultUserModel() {
        return new UserModel(uuid, username, passwordEncoder.encode(password), false);
    }
}
