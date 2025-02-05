package com.madetolive.server.entity

import jakarta.persistence.*


@Entity
@Table(name = "projects")
class ProjectEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private val user: UserEntity,

    @OneToMany(
        targetEntity = TaskEntity::class,
        mappedBy = "project",
        cascade = [CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE],
        orphanRemoval = false)
    private var tasks: List<TaskEntity>? = null,

    @OneToMany(
        targetEntity = Habit::class,
        mappedBy = "project",
        cascade = [CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE],
        orphanRemoval = false)
    private var habits: List<Habit>? = null,

    @Column(nullable = false)
    private var title: String = "",

    @Column(nullable = true)
    private var description: String? = null,

    @Column(nullable = true)
    private var color: String? = null
)