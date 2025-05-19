package com.madetolive.server.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*

@Entity
@Table(name = "reward_bundles")
data class BundleEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    val id: Long = 0,

    @Column(nullable = false)
    @JsonProperty("title")
    val title: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonProperty("project")
    @JoinColumn(name = "project_id", nullable = true)
    @JsonBackReference
    val project: ProjectEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: UserEntity,

    @OneToMany(
        targetEntity = RewardEntity::class,
        mappedBy = "rewardBundle",
        cascade = [CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE],
        orphanRemoval = false
    )
    @JsonManagedReference("reward-bundle")
    val rewards: List<RewardEntity>? = null,

    @Column(nullable = true)
    @JsonProperty("photoFront")
    val photoFront: String?,
)