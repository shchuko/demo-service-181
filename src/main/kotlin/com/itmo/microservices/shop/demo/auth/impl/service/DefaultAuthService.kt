package com.itmo.microservices.shop.demo.auth.impl.service

import com.itmo.microservices.shop.demo.auth.api.model.AuthenticationRequest
import com.itmo.microservices.shop.demo.auth.api.model.AuthenticationResult
import com.itmo.microservices.shop.demo.auth.api.service.AuthService
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class DefaultAuthService(private val tokenManager: JwtTokenManager,
                         private val passwordEncoder: PasswordEncoder) : AuthService {

    override fun authenticate(request: AuthenticationRequest): AuthenticationResult {
        val details = User("foo", "bar", emptyList())
        val accessToken = tokenManager.generateToken(details)
        val refreshToken = tokenManager.generateRefreshToken(details)
        return AuthenticationResult(accessToken, refreshToken)
    }

    override fun refresh(authentication: Authentication): AuthenticationResult {
        val refreshToken = authentication.credentials as String
        val principal = authentication.principal as UserDetails
        val accessToken = tokenManager.generateToken(principal)
        return AuthenticationResult(accessToken, refreshToken)
    }
}
