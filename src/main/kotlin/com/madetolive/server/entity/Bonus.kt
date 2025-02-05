package com.madetolive.server.entity

import jakarta.persistence.*
import java.time.LocalDate


@Entity
@Table(name = "bonus")
class Bonus (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null,

    @OneToMany(mappedBy = "bonus", cascade = [CascadeType.ALL], orphanRemoval = true)
    private val tasks: List<TaskEntity>? = null,

    @OneToMany(mappedBy = "bonus", cascade = [CascadeType.ALL], orphanRemoval = true)
    private val habits: List<Habit>? = null,

    @Column(nullable = false)
    private val type: BonusType,

    @Column(nullable = false)
    private val effect: Float,

    @Column(nullable = false)
    private val startDate: LocalDate,

    @Column(nullable = true)
    private var endDate: LocalDate? = null,

    @Column(nullable = false)
    private var accumulatedDays: Int = 0
)