package com.madetolive.server.entity

import jakarta.persistence.*
import java.time.LocalDate


@Entity
@Table(name = "tasks")
class TaskEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private val user: UserEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = true)
    private var project: ProjectEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schema_id", nullable = true)
    private var schema: TaskSchema? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bonus_id", nullable = true)
    private var bonus: Bonus? = null,

    @Column(nullable = false)
    private var title: String = "",

    @Column(nullable = false)
    private var points: Float = 0.0f,

    @Column(nullable = false)
    private var completed: Boolean = false,

    @Column(nullable = true)
    private var date: LocalDate? = null
) {

    override fun equals(other: Any?): Boolean {
         if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        other as TaskEntity
        return id != null && id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "Task(id=$id, title='$title', points=$points, completed=$completed, date=$date)"
    }

    }
