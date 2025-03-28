package com.madetolive.server.controller

import org.springframework.http.ResponseEntity
import com.madetolive.server.entity.TaskEntity
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
    fun getTasksForCurrentUser(principal: Principal): ResponseEntity<List<TaskEntity>> {
        val username = principal.name // username extracted from JWT
        val user = userRepository.findByUsername(username)
            ?: return ResponseEntity.notFound().build()

        val tasks = taskService.getTasksByUserId(user.id)
        return ResponseEntity.ok(tasks)
    }

    @GetMapping("/by-date")
    fun getTasksByDate(
        principal: Principal,
        @RequestParam  date: Long
    ): ResponseEntity<List<TaskEntity>> {
        val username = principal.name // username extracted from JWT
        val user = userRepository.findByUsername(username)
            ?: return ResponseEntity.notFound().build()
        val localDate = Instant.ofEpochMilli(date)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        val tasks = taskService.getTasksByUserIdAndDate(user.id, localDate)
        return ResponseEntity.ok(tasks)
    }

    @GetMapping("/user/{userId}/completed")
    fun getCompletedTasksByUserId(@PathVariable userId: Long): List<TaskEntity> {
        return taskService.getCompletedTasksByUserId(userId)
    }

    @GetMapping("/user/{userId}/sorted")
    fun getTasksSortedByPoints(@PathVariable userId: Long): List<TaskEntity> {
        return taskService.getTasksByUserIdSortedByPoints(userId)
    }

}
