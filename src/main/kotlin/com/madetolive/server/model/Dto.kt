package com.madetolive.server.model

import com.madetolive.server.entity.FrameEntity
import com.madetolive.server.entity.TaskEntity

// --- DTOs ---

data class CreateTaskRequest(
    val title: String,
    val points: Float,
    val checked: Boolean,
    val date: String,
    val project: ProjectDto
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

data class ProjectDto(
    val id: String,
    val title: String,
    val icon: String,
    val color: String
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
