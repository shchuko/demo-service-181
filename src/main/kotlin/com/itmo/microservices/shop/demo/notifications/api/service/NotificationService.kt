package com.itmo.microservices.shop.demo.notifications.api.service

import com.itmo.microservices.shop.demo.tasks.api.model.TaskModel
import com.itmo.microservices.shop.demo.users.api.model.AppUserModel

interface NotificationService {
    fun processNewUser(user: AppUserModel)
    fun processAssignedTask(task: TaskModel)
}