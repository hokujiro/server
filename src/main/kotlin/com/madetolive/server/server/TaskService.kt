package com.madetolive.server.server

import com.madetolive.server.entity.TaskEntity
import com.madetolive.server.repository.TaskRepository
import org.springframework.stereotype.Service

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
}