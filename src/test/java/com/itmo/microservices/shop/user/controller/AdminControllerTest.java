package com.itmo.microservices.shop.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itmo.microservices.shop.common.security.WithMockCustomUser;
import com.itmo.microservices.shop.common.test.DefaultSecurityTestCase;
import com.itmo.microservices.shop.user.api.exceptions.SecretIsIncorrectException;
import com.itmo.microservices.shop.user.api.exceptions.UserNotFoundException;
import com.itmo.microservices.shop.user.api.model.AdminDTO;
import com.itmo.microservices.shop.user.api.model.UpdateAdminDto;
import com.itmo.microservices.shop.user.impl.service.DefaultAdminService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("dev")
@AutoConfigureMockMvc
public class AdminControllerTest extends DefaultSecurityTestCase {

    private final UUID mockUUID = UUID.fromString("224ec6ce-3fea-11ec-9356-0242ac130003");
    private final AdminDTO adminDTO = new AdminDTO(mockUUID, true);
    private final UpdateAdminDto updateAdminDto = new UpdateAdminDto("123", true);

    @MockBean
    private DefaultAdminService service;

    @Autowired
    private MockMvc mockMvc;

    private final static ObjectMapper mapper = new ObjectMapper();

    //region getInfoIsAdmin
    @Test
    @WithMockCustomUser
    public void whenGetInfoIsAdmin_thenReturnStatusOKAndAdminDTO() throws Exception {
        Mockito.when(service.isIAdmin(Mockito.any())).thenReturn(adminDTO);

        String expectedResponse = mapper.writeValueAsString(adminDTO);

        mockMvc.perform(get("/admins"))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(expectedResponse))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser
    public void whenGetInfoIsAdminWithUnexistedUserUUID_thenReturnStatusNotFoundAndAdminDTO() throws Exception {
        Mockito.when(service.isIAdmin(Mockito.any())).thenThrow(UserNotFoundException.class);

        String expectedResponse = mapper.writeValueAsString(adminDTO);

        mockMvc.perform(get("/admins"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
    //endregion

    //region changeCurrentUserAdminAuthority
    @Test
    @WithMockCustomUser
    public void whenChangeCurrentUserAdminAuthority_thenReturnStatusOKAndAdminDTO() throws Exception {
        Mockito.when(service.updateAdmin(Mockito.any(), Mockito.any())).thenReturn(adminDTO);

        String expectedResponse = mapper.writeValueAsString(adminDTO);
        String responseBody = mapper.writeValueAsString(updateAdminDto);

        mockMvc.perform(put("/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(responseBody)
                )
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(expectedResponse))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockCustomUser
    public void whenChangeCurrentUserAdminAuthorityWithNotExistedUser_thenReturnStatusNotFoundAndAdminDTO() throws Exception {
        Mockito.when(service.updateAdmin(Mockito.any(), Mockito.any())).thenThrow(UserNotFoundException.class);

        String responseBody = mapper.writeValueAsString(updateAdminDto);

        mockMvc.perform(put("/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(responseBody)
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockCustomUser
    public void whenChangeCurrentUserAdminAuthorityWithIncorrectSecret_thenReturnStatusBadRequestAndAdminDTO() throws Exception {
        Mockito.when(service.updateAdmin(Mockito.any(), Mockito.any())).thenThrow(SecretIsIncorrectException.class);

        String responseBody = mapper.writeValueAsString(updateAdminDto);

        mockMvc.perform(put("/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(responseBody)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    //endregion
}
