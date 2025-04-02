package com.madetolive.server.server

import com.madetolive.server.controller.TaskController
import com.madetolive.server.entity.TaskEntity
import com.madetolive.server.entity.UserEntity
import com.madetolive.server.repository.TaskRepository
import com.madetolive.server.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

@Service
class TaskService(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository
) {

    //Working
    fun getTasksListByUserId(userId: Long): List<TaskEntity> {
        return taskRepository.findByUserId(userId)
    }
    //Working
    fun getTaskById(taskId: Long): Optional<TaskEntity?> {
        return taskRepository.findById(taskId)
    }

    fun updateTaskForUser(user: UserEntity, taskId: Long, request: TaskController.CreateTaskRequest): TaskEntity? {
        val existing = taskRepository.findById(taskId).orElse(null) ?: return null
        if (existing.user?.id != user.id) return null

        val wasCompleted = existing.checked
        val oldPoints = existing.points

        val updated = existing.copy(
            title = request.title,
            points = request.points,
            checked = request.checked,
            date = LocalDate.parse(request.date),
            project = existing.project,
            schema = existing.schema,
            bonus = existing.bonus
        )

        val saved = taskRepository.save(updated)

        if (!wasCompleted && updated.checked) {
            user.addPoints(updated.points)
        } else if (wasCompleted && !updated.checked) {
            user.subtractPoints(oldPoints)
        } else if (wasCompleted && updated.checked && updated.points != oldPoints) {
            user.subtractPoints(oldPoints)
            user.addPoints(updated.points)
        }

        userRepository.save(user)
        return saved
    }

    fun deleteTaskForUser(user: UserEntity, taskId: Long): Boolean {
        val task = taskRepository.findById(taskId).orElse(null) ?: return false
        if (task.user?.id != user.id) return false

        if (task.checked) {
            user.subtractPoints(task.points)
            userRepository.save(user)
        }

        taskRepository.delete(task)
        return true
    }

    //TODO
    fun getCompletedTasksByUserId(userId: Long): List<TaskEntity> {
        return taskRepository.findByUserIdAndChecked(userId, true)
    }
    //TODO
    fun getTasksByUserIdSortedByPoints(userId: Long): List<TaskEntity> {
        return taskRepository.findTasksByUserIdOrderByPointsDesc(userId)
    }

    //Working
    fun getTasksByUserIdAndDate(userId: Long, date: LocalDate): List<TaskEntity> {
        return taskRepository.findByUserIdAndDate(userId, date)
    }
    //Working
    fun addTaskForUser(user: UserEntity, task: TaskEntity): TaskEntity {
        task.user = user // 🔥 THIS is crucial
        return taskRepository.save(task)
    }
}