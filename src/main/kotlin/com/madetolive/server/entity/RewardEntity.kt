package com.madetolive.server.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*

@Entity
@Table(name = "rewards")
data class RewardEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    val id: Long = 0,

    @JsonProperty("title")
    @Column(nullable = false)
    val title: String,

    @JsonProperty("points")
    @Column(nullable = true)
    var points: Float = 0.0f,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference("user-reward")
    var user: UserEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonProperty("bundle")
    @JoinColumn(name = "reward_list_id", nullable = true)
    @JsonBackReference("reward-bundle")
    val rewardBundle: BundleEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonProperty("project")
    @JoinColumn(name = "project_id", nullable = true)
    @JsonBackReference
    var project: ProjectEntity? = null,

    @Column(nullable = true)
    @JsonProperty("photo")
    val photo: String?,

    @Column(nullable = false)
    @JsonProperty("reusable")
    val reusable: Boolean = false,

    @Column(nullable = true)
    @JsonProperty("icon")
    var icon: String? = null,

    @Column(nullable = false)
    @JsonProperty("redeemed")
    var redeemed: Boolean = false


)