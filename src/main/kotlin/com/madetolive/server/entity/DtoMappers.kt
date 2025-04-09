package com.madetolive.server.entity

import com.madetolive.server.controller.ProjectController

fun ProjectEntity.toDto(): ProjectController.ProjectDto {
    return ProjectController.ProjectDto(
        id = this.id!!,
        title = this.title,
        subtitle = this.subtitle,
        color = this.color,
        icon = this.icon,
        tasks = this.tasks?.map { it.toDto() } ?: listOf()
    )
}

fun TaskEntity.toDto(): ProjectController.TaskDto {
    return ProjectController.TaskDto(
        id = this.id!!,
        title = this.title,
        checked = this.checked
    )
}