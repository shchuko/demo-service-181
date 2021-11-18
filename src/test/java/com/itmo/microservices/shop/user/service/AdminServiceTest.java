package com.itmo.microservices.shop.user.service;

import com.itmo.microservices.shop.common.test.NoWebSecurityTestCase;
import com.itmo.microservices.shop.user.api.exceptions.SecretIsIncorrectException;
import com.itmo.microservices.shop.user.api.exceptions.UserNotFoundException;
import com.itmo.microservices.shop.user.api.model.AdminDTO;
import com.itmo.microservices.shop.user.api.model.UpdateAdminDto;
import com.itmo.microservices.shop.user.impl.config.SecurityProperties;
import com.itmo.microservices.shop.user.impl.entity.User;
import com.itmo.microservices.shop.user.impl.repository.UserRepository;
import com.itmo.microservices.shop.user.impl.service.DefaultAdminService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@ActiveProfiles("dev")
@AutoConfigureMockMvc
public class AdminServiceTest extends NoWebSecurityTestCase {

    private final User mockUser = createDefaultUser();
    private final UUID mockUUID = UUID.fromString("224ec6ce-3fea-11ec-9356-0242ac130003");
    private final UpdateAdminDto updateAdminDto = new UpdateAdminDto("123", true);

    @MockBean
    private UserRepository repository;

    @MockBean
    private SecurityProperties securityProperties;

    @Autowired
    private DefaultAdminService service;

    @Test
    public void whenUpdateAdmin_ThenReturnAdminDTOAndStatusOK() {
        Mockito.when(securityProperties.getAdminSecret()).thenReturn("123");

        Mockito.when(repository.findById(Mockito.any(UUID.class)))
                .thenReturn(Optional.of(mockUser));
        Mockito.doReturn(mockUser).when(repository).save(Mockito.any(User.class));

        var test = service.updateAdmin(updateAdminDto, mockUUID);
        var correct = new AdminDTO(mockUser.getId(), mockUser.getIsAdmin());

        assertEquals(correct, test);
    }

    @Test
    public void whenUpdateAdminAndUserNotExist_ThenReturnUserNotFoundException() {
        Mockito.when(repository.findById(Mockito.any(UUID.class)))
                .thenReturn(Optional.empty());
        Mockito.when(securityProperties.getAdminSecret()).thenReturn("123");

        try {
            service.updateAdmin(updateAdminDto, mockUUID);
            fail("Correct exception did not throw");
        } catch (Exception test) {
            assertEquals(UserNotFoundException.class, test.getClass());
        }
    }

    @Test
    public void whenUpdateAdminAndUserSecretIsIncorrect_ThenReturnSecretIsIncorrectException() {
        Mockito.when(securityProperties.getAdminSecret()).thenReturn("");
        Mockito.when(repository.findById(Mockito.any(UUID.class)))
                .thenReturn(Optional.of(mockUser));

        try {
            service.updateAdmin(updateAdminDto, mockUUID);
            fail("Correct exception did not throw");
        } catch (Exception test) {
            assertEquals(SecretIsIncorrectException.class, test.getClass());
        }
    }

    @Test
    public void whenIsIAdmin_ThenReturnAdminDTOAndStatusOK() {
        Mockito.when(repository.findById(Mockito.any(UUID.class)))
                .thenReturn(Optional.of(mockUser));

        var test = service.isIAdmin(mockUUID);
        var correct = new AdminDTO(mockUser.getId(), mockUser.getIsAdmin());

        assertEquals(correct, test);
    }

    @Test
    public void whenIsIAdminWithIncorrectUUID_ThenReturnAdminDTOAndStatusOK() {
        Mockito.when(repository.findById(Mockito.any(UUID.class)))
                .thenReturn(Optional.empty());

        try {
            service.isIAdmin(mockUUID);
            fail("Correct exception did not throw");
        } catch (Exception test) {
            assertEquals(UserNotFoundException.class, test.getClass());
        }
    }

    private User createDefaultUser() {
        User user = new User();
        user.setId(mockUUID);
        user.setUsername("user");
        user.setPasswordHash("2123123");
        user.setIsAdmin(true);
        return user;
    }
}
