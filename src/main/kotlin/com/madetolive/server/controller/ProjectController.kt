package com.madetolive.server.controller

import com.madetolive.server.entity.ProjectEntity
import com.madetolive.server.entity.TaskEntity
import com.madetolive.server.entity.UserEntity
import com.madetolive.server.entity.toDto
import com.madetolive.server.repository.UserRepository
import com.madetolive.server.server.ProjectService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api/projects")
class ProjectController (
    val projectService: ProjectService,
    val userRepository: UserRepository,
) {

    @GetMapping("/all")
    fun getProjectsForCurrentUser(
        principal: Principal
    ): ResponseEntity<List<ProjectEntity>> {
        val user = findUser(principal) ?: return ResponseEntity.notFound().build()
        val projects =  projectService.getProjectsByUserId(user.id)
        return ResponseEntity.ok(projects)
    }

    @PostMapping("/add")
    fun addProject(
        principal: Principal,
        @RequestBody request: CreateProjectRequest
    ): ResponseEntity<ProjectEntity> {
        val user = findUser(principal) ?: return ResponseEntity.notFound().build()


        val project = ProjectEntity(
            title = request.title,
            user = user,
            color = request.color,
            icon = request.icon,
            tasks = request.tasksList
        )

        val savedTask = projectService.addProjectForUser(user, project)
        return ResponseEntity.ok(savedTask)
    }

    @GetMapping("/by-id")
    fun getProjectById(@RequestParam id: Long): ResponseEntity<ProjectDto> {
        val project = projectService.getProjectById(id)
        return ResponseEntity.ok(project.toDto())
    }

    private fun findUser(principal: Principal): UserEntity? {
        val username = principal.name
        return userRepository.findByUsername(username)
    }

    data class CreateProjectRequest(
        val title: String,
        val color: String?,
        val icon: String?,
        val tasksList: List<TaskEntity>?
    )

    data class ProjectDto(
        val id: Long,
        val title: String,
        val subtitle: String?,
        val color: String?,
        val icon: String?,
        val tasks: List<TaskDto>
    )

    data class TaskDto(
        val id: Long,
        val title: String,
        val checked: Boolean
    )

}
