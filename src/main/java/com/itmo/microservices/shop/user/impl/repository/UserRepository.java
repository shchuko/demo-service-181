package com.itmo.microservices.shop.user.impl.repository;

import com.itmo.microservices.shop.user.impl.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    User findByUsername(String username);
    Boolean existsUserByUsername(String username);
    Optional<User> findById(UUID uuid);
}
