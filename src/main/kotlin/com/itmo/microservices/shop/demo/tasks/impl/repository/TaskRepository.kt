package com.itmo.microservices.shop.demo.tasks.impl.repository

import com.itmo.microservices.shop.demo.tasks.impl.entity.Task
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface TaskRepository : JpaRepository<Task, UUID> {
    fun deleteAllByAuthor(author: String)
}
