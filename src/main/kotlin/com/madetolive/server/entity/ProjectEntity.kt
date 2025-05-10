package com.madetolive.server.entity

import com.fasterxml.jackson.annotation.JsonManagedReference
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*


@Entity
@Table(name = "projects")
class ProjectEntity (
    @Id
    @JsonProperty("uid")
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
    @JsonManagedReference
    var tasks: List<TaskEntity>? = null,

    @OneToMany(
        targetEntity = TaskEntity::class,
        mappedBy = "project",
        cascade = [CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE],
        orphanRemoval = false)
    @JsonManagedReference
    var rewards: List<RewardEntity>? = null,

    @OneToMany(
        targetEntity = TaskEntity::class,
        mappedBy = "project",
        cascade = [CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE],
        orphanRemoval = false)
    @JsonManagedReference
    var rewardBundles: List<RewardBundleEntity>? = null,

    @OneToMany(
        targetEntity = Habit::class,
        mappedBy = "project",
        cascade = [CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE],
        orphanRemoval = false)
    var habits: List<Habit>? = null,

    @Column(nullable = false)
    var title: String = "",

    @Column(nullable = true)
    var subtitle: String? = null,

    @Column(nullable = true)
    var description: String? = null,

    @Column(nullable = true)
    var color: String? = null,

    @Column(nullable = true)
    var icon: String? = null


) {
    override fun toString(): String {
        return "Project(id=$id, title='$title', description=$description, color=$color, icon =$icon)"
    }
}