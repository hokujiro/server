package com.madetolive.server.repository

import com.madetolive.server.entity.TaskEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate


interface TaskRepository : JpaRepository<TaskEntity?, Long?> {


    // 1. Devolver las tareas por userId
    fun findByUserId(userId: Long): List<TaskEntity>

    // 1. Devolver la tareas por Id
    //fun findById(taskId: Long): TaskEntity

    //fun updateTaskForUser(user: UserEntity, task:TaskEntity)

    // 2. Devuelve las tareas completadas por userId
    fun findByUserIdAndChecked(userId: Long, checked: Boolean): List<TaskEntity>

    // 3. Devuelve las tareas ordenadas por puntos de mayor a menor
    @Query("SELECT t FROM TaskEntity t WHERE t.user.id = :userId ORDER BY t.points DESC")
    fun findTasksByUserIdOrderByPointsDesc(@Param("userId") userId: Long): List<TaskEntity>

    fun findByUserIdAndDate(userId: Long, @Param("date") date: LocalDate): List<TaskEntity>

}