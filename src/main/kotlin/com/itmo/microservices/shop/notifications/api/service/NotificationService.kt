package com.itmo.microservices.shop.notifications.api.service

import com.itmo.microservices.shop.user.api.model.UserModel

interface NotificationService {
    fun processNewUser(user: UserModel)
}
