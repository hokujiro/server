package com.madetolive.server.controller

import com.madetolive.server.entity.PointTransactionEntity
import com.madetolive.server.server.PointTransactionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/points")
class PointTransactionController(
    private val pointTransactionService: PointTransactionService
) {

    // ✅ Add or deduct points for a user
    @PostMapping("/update")
    fun updateUserPoints(
        @RequestBody request: PointUpdateRequest
    ): ResponseEntity<String> {
        pointTransactionService.addPointsToUser(
            userId = request.userId,
            amount = request.points,
            reason = request.reason
        )
        return ResponseEntity.ok("Points updated successfully.")
    }

    // 📜 Get transaction history
    @GetMapping("/history/{userId}")
    fun getUserPointHistory(@PathVariable userId: Long): ResponseEntity<List<PointTransactionEntity>> {
        val history = pointTransactionService.getUserTransactionHistory(userId)
        return ResponseEntity.ok(history)
    }

    // 🧮 Get recalculated total points from transaction log
    @GetMapping("/total/{userId}")
    fun getUserTotalPoints(@PathVariable userId: Long): ResponseEntity<Float> {
        val total = pointTransactionService.getUserTotalPointsFromTransactions(userId)
        return ResponseEntity.ok(total)
    }

    // 🔁 Force recalculation and sync with UserEntity
    @PostMapping("/sync/{userId}")
    fun syncTotalPoints(@PathVariable userId: Long): ResponseEntity<String> {
        pointTransactionService.recalculateAndSyncUserTotalPoints(userId)
        return ResponseEntity.ok("User total points synchronized with transaction log.")
    }
}


data class PointUpdateRequest(
    val userId: Long,
    val points: Float,
    val reason: String
)