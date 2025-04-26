package com.madetolive.server.controller

import com.madetolive.server.entity.FrameEntity
import org.springframework.http.ResponseEntity
import com.madetolive.server.entity.TaskEntity
import com.madetolive.server.entity.UserEntity
import com.madetolive.server.model.*
import com.madetolive.server.repository.FrameRepository
import com.madetolive.server.repository.ProjectRepository
import com.madetolive.server.repository.UserRepository
import com.madetolive.server.server.FrameService
import org.springframework.web.bind.annotation.*
import java.security.Principal


@RestController
@RequestMapping("/api/frames")
class FrameController (
    val frameService: FrameService,
    val userRepository: UserRepository,
    val projectRepository: ProjectRepository,
    val frameRepository: FrameRepository
) {
    @GetMapping("/all")
    fun getFramesForCurrentUser(principal: Principal): ResponseEntity<List<FrameDto>> {
        val user = findUser(principal) ?: return ResponseEntity.notFound().build()
        val tasks = frameService.getFramesListByUserId(user.id)
        return ResponseEntity.ok(tasks.map { it.toDto() })
    }

    @PostMapping("/add")
    fun addFrame(
        principal: Principal,
        @RequestBody request: CreateFrameRequest
    ): ResponseEntity<FrameDto> {
        val user = findUser(principal) ?: return ResponseEntity.notFound().build()

        val frame = FrameEntity(
            title = request.title,
            points = request.points,
            user = user
        )

        request.project.id.takeIf { it.isNotBlank() && it.all { char -> char.isDigit() } }?.toLongOrNull()?.let { projectId ->
            val project = projectRepository.findById(projectId).orElse(null)
            frame.project = project
        }

        val savedTask = frameService.addFrameForUser(user, frame)
        return ResponseEntity.ok(savedTask.toDto())
    }

    @DeleteMapping("/delete/{id}")
    fun deleteFrame(
        principal: Principal,
        @PathVariable id: Long
    ): ResponseEntity<Void> {
        val user = findUser(principal) ?: return ResponseEntity.status(401).build()

        val deleted = frameService.deleteFrameForUser(user, id)
        return if (deleted) ResponseEntity.noContent().build()
        else ResponseEntity.notFound().build()
    }


    private fun findUser(principal: Principal): UserEntity? {
        val username = principal.name
        return userRepository.findByUsername(username)
    }

    @PostMapping("/add-list")
    fun addFrames(
        principal: Principal,
        @RequestBody requests: List<CreateFrameRequest>
    ): ResponseEntity<List<FrameDto>> {
        val user = findUser(principal) ?: return ResponseEntity.status(401).build()

        val frames = requests.map { request ->
            FrameEntity(
                title = request.title,
                points = request.points,
                user = user
            ).also { frame ->
                request.project.id.takeIf { it.isNotBlank() && it.all { char -> char.isDigit() } }
                    ?.toLongOrNull()
                    ?.let { projectId ->
                        val project = projectRepository.findById(projectId).orElse(null)
                        frame.project = project
                    }
            }
        }

        val savedFrames = frameService.addFramesForUser(user, frames)
        return ResponseEntity.ok(savedFrames.map { it.toDto() })
    }

    @DeleteMapping("/delete-list")
    fun deleteFrames(
        principal: Principal,
        @RequestBody frameIds: List<Long>
    ): ResponseEntity<Void> {
        val user = findUser(principal) ?: return ResponseEntity.status(401).build()

        val deleted = frameService.deleteFramesForUser(user, frameIds)
        return if (deleted) ResponseEntity.noContent().build()
        else ResponseEntity.notFound().build()
    }

}
