package com.madetolive.server.controller

import com.madetolive.server.entity.TaskEntity
import com.madetolive.server.server.TaskService
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/tasks")
class TaskController (
    val taskService: TaskService
) {
/*    @GetMapping("/completed/{userId}")
    fun getCompletedTasks(
        @PathVariable userId: Long?,
        @RequestParam startDate: String?,
        @RequestParam endDate: String?
    ): List<TaskRegister> {
        val start = LocalDateTime.parse(startDate)
        val end = LocalDateTime.parse(endDate)

        return taskRegisterService.getCompletedTasks(userId, start, end)
    }*/

    @GetMapping("/user/{userId}")
    fun getTasksByUserId(@PathVariable userId: Long): List<TaskEntity> {
        return taskService.getTasksByUserId(userId)
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
