package com.madetolive.server.entity

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import java.time.LocalDate


@Entity
@Table(name = "tasks")
class TaskEntity(
    @Id
    @JsonProperty("uid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: UserEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonProperty("project")
    @JoinColumn(name = "project_id", nullable = true)
    var project: ProjectEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonProperty("schema")
    @JoinColumn(name = "schema_id", nullable = true)
    var schema: TaskSchema? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonProperty("bonus")
    @JoinColumn(name = "bonus_id", nullable = true)
    var bonus: Bonus? = null,

    @Column(nullable = true)
    @JsonProperty("date")
    var date: LocalDate? = null,

    @Column(nullable = true)
    @JsonProperty("finishingDate")
    var finishingDate: LocalDate? = null,

    @Column(nullable = false)
    @JsonProperty("title")
    var title: String = "",

    @Column(nullable = false)
    @JsonProperty("points")
    var points: Float = 0.0f,

    @Column(nullable = true)
    var checked: Boolean = false

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
        return "Task(id=$id, title='$title', points=$points, completed=$checked, date=$date)"
    }

    fun copy(
        id: Long? = this.id,
        user: UserEntity? = this.user,
        project: ProjectEntity? = this.project,
        schema: TaskSchema? = this.schema,
        bonus: Bonus? = this.bonus,
        date: LocalDate? = this.date,
        title: String = this.title,
        points: Float = this.points,
        checked: Boolean = this.checked,
        finishingDate: LocalDate? = this.finishingDate
    ): TaskEntity {
        return TaskEntity(
            id = id,
            user = user,
            project = project,
            schema = schema,
            bonus = bonus,
            date = date,
            title = title,
            points = points,
            checked = checked,
            finishingDate = finishingDate
        )
    }

}
