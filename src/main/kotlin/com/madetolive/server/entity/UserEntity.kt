package com.madetolive.server.entity

import jakarta.persistence.*

@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long = 0,

    @Column(nullable = false)
    val username: String = "",

    @Column(nullable = true)
    val password: String = "", // Store hashed

    @Column(nullable = true)
    val email: String = "",

    @Column(nullable = true)
    val roles: String = "ROLE_USER", // e.g., "ROLE_USER,ROLE_ADMIN"

    private var totalPoints: Float = 0.0f,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    private val tasks: List<TaskEntity>? = null,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    private val habits: List<Habit>? = null,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    private val projects: List<ProjectEntity>? = null


) {
    fun addPoints(points: Float) {
        totalPoints += points
    }

    fun subtractPoints(points: Float) {
        totalPoints -= points
    }

    fun getTotalPoints(): Float = totalPoints

    fun setTotalPoints(points: Float) {
        totalPoints = points
    }
}