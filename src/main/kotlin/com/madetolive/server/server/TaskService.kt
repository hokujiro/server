package com.madetolive.server.server

import com.madetolive.server.controller.TaskController
import com.madetolive.server.entity.FrameEntity
import com.madetolive.server.entity.TaskEntity
import com.madetolive.server.entity.UserEntity
import com.madetolive.server.model.CreateTaskRequest
import com.madetolive.server.model.DailyPointsSummary
import com.madetolive.server.repository.FrameRepository
import com.madetolive.server.repository.TaskRepository
import com.madetolive.server.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

@Service
class TaskService(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository,
    private val frameRepository: FrameRepository
) {

    fun getTasksListByUserId(userId: Long): List<TaskEntity> {
        return taskRepository.findByUserId(userId)
    }

    fun updateTaskForUser(user: UserEntity, taskId: Long, request: CreateTaskRequest): TaskEntity? {
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

    fun addTasksForUser(user: UserEntity, tasks: List<TaskEntity>): List<TaskEntity> {
        return taskRepository.saveAll(tasks)
    }

    fun deleteTasksForUser(user: UserEntity, taskIds: List<Long>): Boolean {
        val tasks = taskRepository.findAllById(taskIds)
        val userOwnsAll = tasks.all { it?.user?.id == user.id }
        if (!userOwnsAll) return false

        taskRepository.deleteAll(tasks)
        return true
    }

    suspend fun getDailyPointsSummary(userId: Long, date: LocalDate): DailyPointsSummary {
        val tasks = taskRepository.findByUserIdAndDate(userId, date)

        val total = tasks.map { it.points }.sum()
        val positive = tasks.filter { it.points > 0 }.map { it.points }.sum()
        val negative = tasks.filter { it.points < 0 }.map { it.points }.sum()

        return DailyPointsSummary(total, positive, negative)
    }


    fun getCompletedTasksByUserId(userId: Long): List<TaskEntity> {
        return taskRepository.findByUserIdAndChecked(userId, true)
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