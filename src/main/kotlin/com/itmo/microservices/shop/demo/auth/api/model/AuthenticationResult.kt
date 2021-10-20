package com.itmo.microservices.shop.demo.auth.api.model

data class AuthenticationResult(val accessToken: String, val refreshToken: String)
