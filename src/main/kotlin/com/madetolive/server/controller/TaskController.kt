package com.madetolive.server.controller

import org.springframework.http.ResponseEntity
import com.madetolive.server.entity.TaskEntity
import com.madetolive.server.entity.UserEntity
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
) {

    @GetMapping("/all")
    fun getTasksForCurrentUser(
        principal: Principal
    ): ResponseEntity<List<TaskEntity>> {
        val user = findUser(principal) ?: return ResponseEntity.notFound().build()
        val tasks = taskService.getTasksListByUserId(user.id)
        return ResponseEntity.ok(tasks)
    }

    @GetMapping("/by-date")
    fun getTasksByDate(
        principal: Principal,
        @RequestParam  date: Long
    ): ResponseEntity<List<TaskEntity>> {
        val user = findUser(principal) ?: return ResponseEntity.notFound().build()
        val localDate = Instant.ofEpochMilli(date)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        val tasks = taskService.getTasksByUserIdAndDate(user.id, localDate)
        return ResponseEntity.ok(tasks)
    }

    @PostMapping("/add")
    fun addTask(
        principal: Principal,
        @RequestBody request: CreateTaskRequest
    ): ResponseEntity<TaskEntity> {
        val user = findUser(principal) ?: return ResponseEntity.notFound().build()

        val localDate = LocalDate.parse(request.date)

        val task = TaskEntity(
            title = request.title,
            points = request.points,
            checked = request.checked,
            date = localDate,
            user = user
            // other fields can be defaulted or added as needed
        )

        val savedTask = taskService.addTaskForUser(user, task)
        return ResponseEntity.ok(savedTask)
    }

    @PutMapping("/update/{id}")
    fun updateTask(
        principal: Principal,
        @PathVariable id: Long,
        @RequestBody request: CreateTaskRequest
    ): ResponseEntity<TaskEntity> {
        val user = findUser(principal) ?: return ResponseEntity.status(401).build()

        val updatedTask = taskService.updateTaskForUser(
            user = user,
            taskId = id,
            request = request
        ) ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(updatedTask)
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

    data class CreateTaskRequest(
        val title: String,
        val points: Float,
        val checked: Boolean,
        val date: String
    )

}
