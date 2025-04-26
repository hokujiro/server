package com.madetolive.server.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import java.time.LocalDate


@Entity
@Table(name = "frames")
class FrameEntity(
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
    @JsonBackReference
    var project: ProjectEntity? = null,

    @Column(nullable = false)
    @JsonProperty("title")
    var title: String = "",

    @Column(nullable = false)
    @JsonProperty("points")
    var points: Float = 0.0f,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        other as FrameEntity
        return id != null && id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "Task(id=$id, title='$title', points=$points)"
    }

    fun copy(
        id: Long? = this.id,
        user: UserEntity? = this.user,
        project: ProjectEntity? = this.project,
        title: String = this.title,
        points: Float = this.points,
    ): FrameEntity {
        return FrameEntity(
            id = id,
            user = user,
            project = project,
            title = title,
            points = points,
        )
    }
}
