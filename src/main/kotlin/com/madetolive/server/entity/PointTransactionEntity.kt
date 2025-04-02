package com.madetolive.server.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name ="point_transactions")
data class PointTransactionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity,

    @Column(nullable = false)
    val points: Float, // Can be positive (gain) or negative (loss)

    @Column(nullable = false)
    val reason: String, // e.g., "TASK_COMPLETED", "TASK_UNDONE", "BONUS", etc.

    @Column(nullable = false)
    val timestamp: LocalDateTime = LocalDateTime.now()
)