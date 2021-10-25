package com.itmo.microservices.shop.demo.notifications.impl.repository

import com.itmo.microservices.shop.demo.notifications.impl.entity.NotificationUser
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationUserRepository: JpaRepository<NotificationUser, String>