package com.itmo.microservices.shop.notifications.impl.service

import com.itmo.microservices.shop.notifications.api.service.NotificationService
import com.itmo.microservices.shop.order.api.model.OrderDTO
import com.itmo.microservices.shop.user.api.model.UserModel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class StubNotificationService() : NotificationService {

    companion object {
        val log: Logger = LoggerFactory.getLogger(StubNotificationService::class.java)
    }

    override fun processNewUser(user: UserModel) {

        log.info("User ${user.username} was created & should be notified")
    }

    override fun processOrderFinalized(order: OrderDTO) {
        log.info("Order ${order.uuid} was created & user should be notified")
    }

}
