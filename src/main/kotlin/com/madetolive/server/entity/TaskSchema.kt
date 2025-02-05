package com.madetolive.server.entity

import jakarta.persistence.*


@Entity
@Table(name = "schemas")
class TaskSchema (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null,

    @OneToMany(
        targetEntity = TaskEntity::class,
        mappedBy = "schema",
        cascade = [CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE],
        orphanRemoval = false)
    private var tasks: List<TaskEntity>? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = true)
    private var project: ProjectEntity? = null,

    @Column(nullable = false)
    private var points: Float = 0.0f,

    @Column(nullable = false)
    private var title: String = "",

    @Column(length = 5000, nullable = true)
    private var description: String? = null,

    )