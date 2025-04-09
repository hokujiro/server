package com.madetolive.server.server

import com.madetolive.server.entity.RefreshTokenEntity
import com.madetolive.server.repository.RefreshTokenRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository
) {
    fun createRefreshToken(userId: Long): String {
        val token = UUID.randomUUID().toString()
        val expiration = LocalDateTime.now().plusDays(7)

        val entity = RefreshTokenEntity(
            userId = userId,
            token = token,
            expiresAt = expiration
        )
        refreshTokenRepository.save(entity)

        return token
    }

    fun validateToken(token: String): RefreshTokenEntity? {
        val storedToken = refreshTokenRepository.findByToken(token)
        return if (storedToken != null && storedToken.expiresAt.isAfter(LocalDateTime.now())) {
            storedToken
        } else {
            null
        }
    }

    fun deleteUserTokens(userId: Long) {
        refreshTokenRepository.deleteByUserId(userId)
    }
}