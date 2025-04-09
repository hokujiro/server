package com.madetolive.server

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.google.api.client.util.Value
import com.madetolive.server.config.JwtConfig
import com.madetolive.server.entity.UserEntity
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtService(
    private val jwtConfig: JwtConfig
) {
    private val secret = jwtConfig.secret
    private val expirationTime = 24 * 60 * 60 * 1000 // 1 day

    fun generateToken(user: UserEntity): String {
        return JWT.create()
            .withSubject(user.username)
            .withClaim("userId", user.id)
            .withClaim("name", user.username)
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + expirationTime))
            .sign(Algorithm.HMAC256(secret))
    }

    fun generateRefreshToken(username: String): String {
        return JWT.create()
            .withSubject(username)
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)) // 7 days
            .sign(Algorithm.HMAC256(secret))
    }

    fun extractUsername(token: String): String {
        return JWT.require(Algorithm.HMAC256(secret))
            .build()
            .verify(token)
            .subject
    }

    fun validateToken(token: String, username: String): Boolean {
        return extractUsername(token) == username && !isTokenExpired(token)
    }

    fun isTokenExpired(token: String): Boolean {
        val decoded = JWT.require(Algorithm.HMAC256(secret)).build().verify(token)
        return decoded.expiresAt.before(Date())
    }
}

data class RefreshTokenRequest(
    val refreshToken: String
)
data class TokenResponse(
    val accessToken: String,
    val refreshToken: String
)