package com.itmo.microservices.shop.user.impl.service;

import com.itmo.microservices.commonlib.annotations.InjectEventLogger;
import com.itmo.microservices.commonlib.logging.EventLogger;
import com.itmo.microservices.shop.user.api.exceptions.SecretIsIncorrectException;
import com.itmo.microservices.shop.user.api.exceptions.UserNotFoundException;
import com.itmo.microservices.shop.user.api.model.AdminDTO;
import com.itmo.microservices.shop.user.api.model.UpdateAdminDto;
import com.itmo.microservices.shop.user.api.service.AdminService;
import com.itmo.microservices.shop.user.impl.config.SecurityProperties;
import com.itmo.microservices.shop.user.impl.logging.AdminServiceNotableEvent;
import com.itmo.microservices.shop.user.impl.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DefaultAdminService implements AdminService{

    private final UserRepository userRepository;
    private final SecurityProperties securityProperties;

    @InjectEventLogger
    EventLogger eventLogger;

    DefaultAdminService(UserRepository userRepository, JwtTokenManager jwtTokenManager, SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
        this.userRepository = userRepository;
    }

    @Override
    public AdminDTO updateAdmin(UpdateAdminDto updateAdminDto, UUID currentUserUUID) {
        if (!securityProperties.getAdminSecret().equals(updateAdminDto.getAdminSecret())) {
            eventLogger.error(AdminServiceNotableEvent.E_ADMIN_SECRET_IS_INCORRECT, currentUserUUID);
            throw new SecretIsIncorrectException("Inputted secret is incorrect");
        }
        var userOptional = userRepository.findById(currentUserUUID);
        if (userOptional.isEmpty()) {
            eventLogger.error(AdminServiceNotableEvent.E_USER_NOT_FOUND, currentUserUUID);
            throw new UserNotFoundException("User with UUID " + currentUserUUID + " not found");
        }
        var user = userOptional.get();
        if (user.getIsAdmin() != updateAdminDto.getIsAdmin()) {
            user.setIsAdmin(updateAdminDto.getIsAdmin());
            userRepository.save(user);
            eventLogger.info(AdminServiceNotableEvent.I_UPDATE_ADMIN_REQUEST,
                    String.format("UserUUID: %s, isAdmin: %s", currentUserUUID, updateAdminDto.getIsAdmin()));
        }
        return new AdminDTO(user.getId(), user.getIsAdmin());
    }

    @Override
    public AdminDTO isIAdmin(UUID currentUserUUID) {
        var userOptional = userRepository.findById(currentUserUUID);
        if (userOptional.isEmpty()) {
            eventLogger.error(AdminServiceNotableEvent.E_USER_NOT_FOUND, currentUserUUID);
            throw new UserNotFoundException("User with UUID " + currentUserUUID + " not found");
        }
        var user = userOptional.get();
        eventLogger.info(AdminServiceNotableEvent.I_IS_ADMIN_REQUEST,
                String.format("UserUUID: %s, isAdmin: %s", currentUserUUID, user.getIsAdmin()));
        return new AdminDTO(user.getId(),user.getIsAdmin());
    }
}
