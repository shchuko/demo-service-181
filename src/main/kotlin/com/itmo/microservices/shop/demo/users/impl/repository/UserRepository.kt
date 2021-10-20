package com.itmo.microservices.shop.demo.users.impl.repository

import org.springframework.data.jpa.repository.JpaRepository
import com.itmo.microservices.shop.demo.users.impl.entity.AppUser
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<AppUser, String>