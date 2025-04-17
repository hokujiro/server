package com.madetolive.server.controller

import com.madetolive.server.entity.ProjectEntity
import com.madetolive.server.entity.TaskEntity
import com.madetolive.server.entity.UserEntity
import com.madetolive.server.repository.UserRepository
import com.madetolive.server.server.ProjectService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal
import org.slf4j.LoggerFactory


@RestController
@RequestMapping("/api/projects")
class ProjectController(
    private val projectService: ProjectService,
    private val userRepository: UserRepository,
) {
    private val logger = LoggerFactory.getLogger("ProjectController")

    @GetMapping("/all")
    fun getProjectsForCurrentUser(
        principal: Principal
    ): ResponseEntity<List<ProjectDto>> {
        val user = findUser(principal) ?: return ResponseEntity.notFound().build()
        val projects = projectService.getProjectsByUserId(user.id)
        return ResponseEntity.ok(projects.map { it.toDto() })
    }

    @PostMapping("/add")
    fun addProject(
        principal: Principal,
        @RequestBody request: CreateProjectRequest
    ): ResponseEntity<ProjectDto> {
        val user = findUser(principal) ?: return ResponseEntity.notFound().build()

        val project = ProjectEntity(
            title = request.title,
            color = request.color,
            icon = request.icon,
            user = user,
            tasks = request.tasks?.map {
                TaskEntity(
                    title = it.title,
                    checked = it.checked,
                    points = it.points,
                    user = user
                )
            }
        )

        project.tasks?.forEach { it.project = project }

        val savedProject = projectService.addProjectForUser(user, project)
        logger.debug("Saved project: {}", savedProject)
        return ResponseEntity.ok(savedProject.toDto())
    }

    @GetMapping("/by-id")
    fun getProjectById(@RequestParam id: Long): ResponseEntity<ProjectDto> {
        val project = projectService.getProjectById(id)
        return ResponseEntity.ok(project.toDto())
    }

    @DeleteMapping("/delete/{id}")
    fun deleteProject(
        principal: Principal,
        @PathVariable id: Long
    ): ResponseEntity<Void> {
        val user = findUser(principal) ?: return ResponseEntity.status(401).build()
        val deleted = projectService.deleteProjectForUser(user, id)
        return if (deleted) ResponseEntity.noContent().build()
        else ResponseEntity.notFound().build()
    }

    private fun findUser(principal: Principal): UserEntity? {
        return userRepository.findByUsername(principal.name)
    }

    // DTOs & Mappers

    data class CreateProjectRequest(
        val title: String,
        val color: String?,
        val icon: String?,
        val tasks: List<SimpleTaskRequest>? = null
    )

    data class SimpleTaskRequest(
        val title: String,
        val checked: Boolean,
        val points: Float
    )

    data class ProjectDto(
        val uid: Long,
        val title: String,
        val color: String?,
        val icon: String?,
        val tasks: List<TaskDto>
    )

    data class TaskDto(
        val uid: Long,
        val title: String,
        val checked: Boolean,
        val points: Float
    )

    fun ProjectEntity.toDto(): ProjectDto = ProjectDto(
        uid = this.id!!,
        title = this.title,
        color = this.color,
        icon = this.icon,
        tasks = this.tasks?.map { it.toDto() } ?: emptyList()
    )

    fun TaskEntity.toDto(): TaskDto = TaskDto(
        uid = this.id!!,
        title = this.title,
        checked = this.checked,
        points = this.points
    )
}