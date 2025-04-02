package com.madetolive.server.server

import com.madetolive.server.entity.PointTransactionEntity
import com.madetolive.server.repository.PointTransactionRepository
import com.madetolive.server.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PointTransactionService(
    private val pointTransactionRepository: PointTransactionRepository,
    private val userRepository: UserRepository
) {

    @Transactional
    fun addPointsToUser(userId: Long, amount: Float, reason: String) {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found") }

        // Create transaction record
        val transaction = PointTransactionEntity(
            user = user,
            points = amount,
            reason = reason
        )
        pointTransactionRepository.save(transaction)

        // Update user balance
        user.addPoints(amount)
        userRepository.save(user)
    }

    @Transactional(readOnly = true)
    fun getUserTransactionHistory(userId: Long): List<PointTransactionEntity> {
        return pointTransactionRepository.findAllByUserId(userId)
    }

    @Transactional(readOnly = true)
    fun getUserTotalPointsFromTransactions(userId: Long): Float {
        return pointTransactionRepository.sumPointsByUserId(userId) ?: 0.0f
    }

    // Optional: Sync the recalculated value into UserEntity if needed
    @Transactional
    fun recalculateAndSyncUserTotalPoints(userId: Long) {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found") }

        val recalculatedTotal = getUserTotalPointsFromTransactions(userId)
        user.setTotalPoints(recalculatedTotal)
        userRepository.save(user)
    }
}