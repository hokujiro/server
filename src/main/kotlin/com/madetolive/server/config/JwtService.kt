package com.madetolive.server.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.madetolive.server.entity.UserEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtService {
    private val secret = "your_secret_key" // Use environment variables for secrets
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