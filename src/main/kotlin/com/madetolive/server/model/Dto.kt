package com.madetolive.server.model

import com.madetolive.server.entity.FrameEntity
import com.madetolive.server.entity.RewardEntity
import com.madetolive.server.entity.TaskEntity

// --- DTOs ---

data class CreateTaskRequest(
    val title: String,
    val points: Float,
    val checked: Boolean,
    val date: String,
    val project: ProjectDto
)

data class CreateRewardRequest(
    val title: String,
    val points: Float,
    val project: ProjectDto,
    val bundle: BundleDto,
    val photoUrl: String?,
    val icon: String?,
    val reusable: Boolean,
    val redeemed: Boolean
)

data class DailyPointsSummary(
    val total: Float,
    val positive: Float,
    val negative: Float
)

data class TaskDto(
    val uid: Long?,
    val title: String,
    val points: Float,
    val checked: Boolean,
    val date: String,
    val project: ProjectDto?
)

data class RewardDto(
    val id: Long?,
    val title: String,
    val points: Float?,
    val reusable: Boolean,
    val photoUrl: String?,
    val project: ProjectDto?,
    val bundle: BundleDto?,
    val icon: String?,
    val redeemed: Boolean
)

data class ProjectDto(
    val id: String,
    val title: String,
    val icon: String,
    val color: String
)

data class BundleDto(
    val id: String,
    val title: String,
    val project: ProjectDto?,
    val photo: String?
)


data class CreateFrameRequest(
    val title: String,
    val points: Float,
    val project: ProjectDto
)

data class FrameDto(
    val uid: Long?,
    val title: String,
    val points: Float,
    val project: ProjectDto?
)

// --- Mapper ---

fun TaskEntity.toDto(): TaskDto = TaskDto(
    uid = this.id,
    title = this.title,
    points = this.points,
    checked = this.checked,
    date = this.date.toString(),
    project = this.project?.let {
        ProjectDto(
            id = it.id.toString(),
            title = it.title,
            icon = it.icon?: "Icon",
            color = it.color?: "Color"
        )
    }
)

fun FrameEntity.toDto(): FrameDto = FrameDto(
    uid = this.id,
    title = this.title,
    points = this.points,
    project = this.project?.let {
        ProjectDto(
            id = it.id.toString(),
            title = it.title,
            icon = it.icon?: "Icon",
            color = it.color?: "Color"
        )
    }
)

fun RewardEntity.toDto(): RewardDto = RewardDto(
    id = this.id,
    title = this.title,
    points = this.points,
    project = this.project?.let {
        ProjectDto(
            id = it.id.toString(),
            title = it.title,
            icon = it.icon?: "Icon",
            color = it.color?: "Color"
        )
    },
    reusable = this.reusable,
    icon = this.icon,
    photoUrl = this.photoUrl,
    bundle = this.rewardBundle?.let { bundle ->
        BundleDto(
            id = bundle.id.toString(),
            title = bundle.title,
            photo = bundle.photoFront,
            project = this.project?.let {
                ProjectDto(
                    id = it.id.toString(),
                    title = it.title,
                    icon = it.icon?: "Icon",
                    color = it.color?: "Color"
                )
            },
        )
    },
    redeemed = this.redeemed
)
