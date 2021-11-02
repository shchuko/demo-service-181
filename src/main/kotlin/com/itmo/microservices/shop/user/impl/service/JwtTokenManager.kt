package com.itmo.microservices.shop.user.impl.service

import com.itmo.microservices.shop.user.impl.config.SecurityProperties
import com.itmo.microservices.shop.user.impl.userdetails.UserAuth
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant
import java.util.*

@Component
class JwtTokenManager(private val properties: SecurityProperties) {

    /**
     * Validate received token & check if it matches with user details
     * @param token Token data
     * @return User info retrieved from token
     */
    fun readAccessToken(token: String): UserAuth {
        val uuid = UUID.fromString(getIdFromToken(token))
        val username = getUsernameFromToken(token)
        val type = getClaimFromToken(token) { it["type"] }
        if (TokenType.ACCESS.name.lowercase(Locale.getDefault()) != type)
            throw IllegalArgumentException("Token is not of ACCESS type")
        val roles = mutableListOf<SimpleGrantedAuthority>()
        roles.add(SimpleGrantedAuthority("ACCESS"))
        getRolesFromToken(token).forEach { role -> roles.add(SimpleGrantedAuthority(role)) }
        return UserAuth(uuid, username, token, roles)
    }

    /**
     * Validate received refresh token & check if it matches with user details
     * @param token Token data
     * @return User info retrieved from token
     */
    fun readRefreshToken(token: String): UserAuth {
        val uuid = UUID.fromString(getIdFromToken(token))
        val username = getUsernameFromToken(token)
        val type = getClaimFromToken(token) { it["type"] }
        if (TokenType.REFRESH.name.lowercase(Locale.getDefault()) != type)
            throw IllegalArgumentException("Token is not of REFRESH type")
        return UserAuth(uuid, username, token, mutableListOf(SimpleGrantedAuthority("REFRESH")))
    }

    fun <T> getClaimFromToken(token: String, claimsResolver: (Claims) -> T): T {
        return claimsResolver(getAllClaimsFromToken(token))
    }

    //retrieve username from jwt token
    fun getUsernameFromToken(token: String): String {
        return getClaimFromToken(token) { it.subject }
    }

    fun getIdFromToken(token: String): String = getClaimFromToken(token) { it.id }

    //retrieve roles from jwt token
    fun getRolesFromToken(token: String): List<String> {
        return getClaimFromToken(token) { claims ->
            ((claims.get("roles", List::class.java) ?: emptyList<String>()) as List<*>)
                .filterIsInstance(String::class.java)
        }
    }

    //retrieve expiration date from jwt token
    fun getExpirationDateFromToken(token: String): Date {
        return getClaimFromToken(token) { it.expiration }
    }

    //check if the token has expired
    private fun isTokenExpired(token: String): Boolean {
        val expiration = getExpirationDateFromToken(token)
        return expiration.before(Date())
    }

    /**
     * Generate token for user specified by UserDetails
     * @param userAuth User information
     * @return Generated token
     */
    fun generateToken(userAuth: UserAuth): String =
        doGenerateToken(userAuth, TokenType.ACCESS, properties.tokenLifeTime)

    /**
     * Generate refresh token for user specified by UserDetails
     * @param userAuth User information
     * @return Generated token
     */
    fun generateRefreshToken(userAuth: UserAuth): String = doGenerateToken(
        userAuth, TokenType.REFRESH,
        properties.refreshTokenLifeTime
    )

    //for retrieving any information from token we will need the secret key
    private fun getAllClaimsFromToken(token: String): Claims = Jwts.parser()
        .setSigningKey(properties.secret)
        .parseClaimsJws(token)
        .body

    private fun doGenerateToken(
        userAuth: UserAuth,
        type: TokenType,
        tokenTTL: Duration
    ): String =
        Jwts.builder()
            .claim("type", type.name.lowercase(Locale.getDefault()))
            .setId(userAuth.uuid.toString())
            .setSubject(userAuth.username)
            .setIssuedAt(Date())
            .setExpiration(Date.from(Instant.now().plus(tokenTTL)))
            .claim("roles", userAuth.authorities.map { it.authority })
            .signWith(SignatureAlgorithm.HS512, properties.secret)
            .compact()

    private enum class TokenType {
        ACCESS, REFRESH
    }
}