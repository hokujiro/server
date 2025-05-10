package com.madetolive.server.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*

@Entity
@Table(name = "reward_bundles")
data class RewardBundleEntity(
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

    @OneToMany(mappedBy = "rewardList", cascade = [CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE])
    @JsonManagedReference
    val rewards: List<RewardEntity>? = null,

    @Column(nullable = true)
    @JsonProperty("photoFront")
    val photoFront: String?,
)