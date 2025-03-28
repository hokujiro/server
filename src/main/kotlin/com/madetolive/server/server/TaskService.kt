package com.madetolive.server.server

import com.madetolive.server.entity.TaskEntity
import com.madetolive.server.entity.UserEntity
import com.madetolive.server.repository.TaskRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class TaskService(
    private val taskRepository: TaskRepository
) {

    fun getTasksByUserId(userId: Long): List<TaskEntity> {
        return taskRepository.findByUserId(userId)
    }

    fun getCompletedTasksByUserId(userId: Long): List<TaskEntity> {
        return taskRepository.findByUserIdAndCompleted(userId, true)
    }

    fun getTasksByUserIdSortedByPoints(userId: Long): List<TaskEntity> {
        return taskRepository.findTasksByUserIdOrderByPointsDesc(userId)
    }

    fun getTasksByUserIdAndDate(userId: Long, date: LocalDate): List<TaskEntity> {
        return taskRepository.findByUserIdAndDate(userId, date)
    }

    fun addTaskForUser(user: UserEntity, task: TaskEntity): TaskEntity {
        task.user = user // 🔥 THIS is crucial
        return taskRepository.save(task)
    }
}