package com.itmo.microservices.shop.user.api.service;

import com.itmo.microservices.shop.user.api.model.AdminDTO;
import com.itmo.microservices.shop.user.api.model.UpdateAdminDto;

import java.util.UUID;

public interface AdminService {
    AdminDTO updateAdmin(UpdateAdminDto updateAdminDto, UUID currentUserUUID);

    AdminDTO isIAdmin(UUID userUUID);
}
