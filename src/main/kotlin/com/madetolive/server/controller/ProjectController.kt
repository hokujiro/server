package com.madetolive.server.controller

import com.madetolive.server.entity.ProjectEntity
import com.madetolive.server.entity.TaskEntity
import com.madetolive.server.entity.UserEntity
import com.madetolive.server.repository.UserRepository
import com.madetolive.server.server.ProjectService
import com.madetolive.server.server.TaskService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

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
        val tasks =  projectService.getProjectsByUserId(user.id)
        return ResponseEntity.ok(tasks)
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
            color = request.color
            // other fields can be defaulted or added as needed
        )

        val savedTask = projectService.addProjectForUser(user, project)
        return ResponseEntity.ok(savedTask)
    }

    private fun findUser(principal: Principal): UserEntity? {
        val username = principal.name
        return userRepository.findByUsername(username)
    }

    data class CreateProjectRequest(
        val title: String,
        val color: String
    )

}
