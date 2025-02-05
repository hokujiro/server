package com.madetolive.server.entity

import jakarta.persistence.*

//DigitalOcean
@Entity
@Table(name = "users")
data class UserEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long,

    @Column(nullable = false)
    val username: String = "",

    @Column(nullable = false)
    val password: String, // Store hashed

    @Column(nullable = false)
    val email: String,

    @Column(unique = true, nullable = true)
    val googleId: String?,

    @Column(nullable = false)
    val roles: String, // e.g., "ROLE_USER,ROLE_ADMIN"

    private var totalPoints: Float = 0.0f,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    private val tasks: List<TaskEntity>? = null,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    private val habits: List<Habit>? = null,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    private val projects: List<ProjectEntity>? = null
)