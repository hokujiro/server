package com.madetolive.server.entity

import jakarta.persistence.*


@Entity
@Table(name = "projects")
class ProjectEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: UserEntity,

    @OneToMany(
        targetEntity = TaskEntity::class,
        mappedBy = "project",
        cascade = [CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE],
        orphanRemoval = false)
    var tasks: List<TaskEntity>? = null,

    @OneToMany(
        targetEntity = Habit::class,
        mappedBy = "project",
        cascade = [CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE],
        orphanRemoval = false)
    var habits: List<Habit>? = null,

    @Column(nullable = false)
    var title: String = "",

    @Column(nullable = true)
    var description: String? = null,

    @Column(nullable = true)
    var color: String? = null
)