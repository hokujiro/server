package com.madetolive.server.entity

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "reward_transactions")
data class RewardTransactionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reward_id", nullable = false)
    @JsonProperty("reward")
    val reward: RewardEntity,

    @Column(nullable = false)
    @JsonProperty("date")
    val date: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = true)
    @JsonProperty("photoStamp")
    val photoStamp: String?
)