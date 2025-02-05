package com.madetolive.server.entity

import jakarta.persistence.*


@Entity
@Table(name = "habits")
class Habit (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private val user: UserEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private val project: ProjectEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bonus_id", nullable = true)
    private var bonus: Bonus? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private val type: HabitType = HabitType.POSITIVE,

    @Column(nullable = false)
    private var title: String = "",

    @Column(nullable = false)
    private var points: Float? = null,

    @Column(nullable = false)
    private var positive_pulses: Int = 0,

    @Column(nullable = false)
    private var negative_pulses: Int = 0

)