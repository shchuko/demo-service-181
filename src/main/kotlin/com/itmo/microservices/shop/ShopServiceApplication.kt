package com.itmo.microservices.shop

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ShopServiceApplication

fun main(args: Array<String>) {
    runApplication<ShopServiceApplication>(*args)
}