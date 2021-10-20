package com.itmo.microservices.shop.demo.users.api.messaging

import com.itmo.microservices.shop.demo.users.api.model.AppUserModel

data class UserCreatedEvent(val user: AppUserModel)

data class UserDeletedEvent(val username: String)
