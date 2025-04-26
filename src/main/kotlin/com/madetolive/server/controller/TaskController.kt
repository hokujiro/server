package com.madetolive.server.controller

import com.madetolive.server.entity.FrameEntity
import org.springframework.http.ResponseEntity
import com.madetolive.server.entity.TaskEntity
import com.madetolive.server.entity.UserEntity
import com.madetolive.server.model.*
import com.madetolive.server.repository.ProjectRepository
import com.madetolive.server.repository.UserRepository
import com.madetolive.server.server.TaskService
import org.springframework.web.bind.annotation.*
import java.security.Principal
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId


@RestController
@RequestMapping("/api/tasks")
class TaskController (
    val taskService: TaskService,
    val userRepository: UserRepository,
    val projectRepository: ProjectRepository,
) {

    @GetMapping("/all")
    fun getTasksForCurrentUser(principal: Principal): ResponseEntity<List<TaskDto>> {
        val user = findUser(principal) ?: return ResponseEntity.notFound().build()
        val tasks = taskService.getTasksListByUserId(user.id)
        return ResponseEntity.ok(tasks.map { it.toDto() })
    }

    @GetMapping("/by-date")
    fun getTasksByDate(
        principal: Principal,
        @RequestParam  date: Long
    ): ResponseEntity<List<TaskDto>> {
        val user = findUser(principal) ?: return ResponseEntity.notFound().build()
        val localDate = Instant.ofEpochMilli(date)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        val tasks = taskService.getTasksByUserIdAndDate(user.id, localDate)
        return ResponseEntity.ok(tasks.map { it.toDto() })
    }

    @PostMapping("/add")
    fun addTask(
        principal: Principal,
        @RequestBody request: CreateTaskRequest
    ): ResponseEntity<TaskDto> {
        val user = findUser(principal) ?: return ResponseEntity.notFound().build()
        val localDate = LocalDate.parse(request.date)

        val task = TaskEntity(
            title = request.title,
            points = request.points,
            checked = request.checked,
            date = localDate,
            user = user
        )

        request.project.id.takeIf { it.isNotBlank() && it.all { char -> char.isDigit() } }?.toLongOrNull()?.let { projectId ->
            val project = projectRepository.findById(projectId).orElse(null)
            task.project = project
        }

        val savedTask = taskService.addTaskForUser(user, task)
        return ResponseEntity.ok(savedTask.toDto())
    }


    @PutMapping("/update/{id}")
    fun updateTask(
        principal: Principal,
        @PathVariable id: Long,
        @RequestBody request: CreateTaskRequest
    ): ResponseEntity<TaskDto> {
        val user = findUser(principal) ?: return ResponseEntity.status(401).build()

        val updatedTask = taskService.updateTaskForUser(
            user,
            id,
            request
        )
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(updatedTask.toDto())
    }

    @DeleteMapping("/delete/{id}")
    fun deleteTask(
        principal: Principal,
        @PathVariable id: Long
    ): ResponseEntity<Void> {
        val user = findUser(principal) ?: return ResponseEntity.status(401).build()

        val deleted = taskService.deleteTaskForUser(user, id)
        return if (deleted) ResponseEntity.noContent().build()
        else ResponseEntity.notFound().build()
    }

    @PostMapping("/add-list")
    fun addTaskList(
        principal: Principal,
        @RequestBody requests: List<CreateTaskRequest>
    ): ResponseEntity<List<TaskDto>> {
        val user = findUser(principal) ?: return ResponseEntity.status(401).build()

        val tasks = requests.map { request ->
            TaskEntity(
                title = request.title,
                points = request.points,
                checked = request.checked,
                date = LocalDate.parse(request.date),
                user = user
            ).also { task ->
                request.project.id.takeIf { it.isNotBlank() && it.all { char -> char.isDigit() } }
                    ?.toLongOrNull()
                    ?.let { projectId ->
                        val project = projectRepository.findById(projectId).orElse(null)
                       task.project = project
                    }
            }
        }

        val savedTasks = taskService.addTasksForUser(user, tasks)
        return ResponseEntity.ok(savedTasks.map { it.toDto() })
    }

    @DeleteMapping("/delete-list")
    fun deleteTasks(
        principal: Principal,
        @RequestBody taskIds: List<Long>
    ): ResponseEntity<Void> {
        val user = findUser(principal) ?: return ResponseEntity.status(401).build()

        val deleted = taskService.deleteTasksForUser(user, taskIds)
        return if (deleted) ResponseEntity.noContent().build()
        else ResponseEntity.notFound().build()
    }



    @GetMapping("/points-summary")
    suspend fun getPointsSummary(
        principal: Principal,
        @RequestParam("date") date: Long
    ): ResponseEntity<DailyPointsSummary> {
        val user = findUser(principal) ?: return ResponseEntity.status(401).build()
        val localDate = Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDate()
        val summary = taskService.getDailyPointsSummary(user.id, localDate)
        return ResponseEntity.ok(summary)
    }


    @GetMapping("/user/{userId}/completed")
    fun getCompletedTasksByUserId(@PathVariable userId: Long): List<TaskEntity> {
        return taskService.getCompletedTasksByUserId(userId)
    }

    @GetMapping("/user/{userId}/sorted")
    fun getTasksSortedByPoints(@PathVariable userId: Long): List<TaskEntity> {
        return taskService.getTasksByUserIdSortedByPoints(userId)
    }

    private fun findUser(principal: Principal): UserEntity? {
        val username = principal.name
        return userRepository.findByUsername(username)
    }

}
