package com.madetolive.server.repository

import com.madetolive.server.entity.TaskEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param


interface TaskRepository : JpaRepository<TaskEntity?, Long?> {


    // 1. Devolver las tareas por userId
    fun findByUserId(userId: Long): List<TaskEntity>

    // 2. Devuelve las tareas completadas por userId
    fun findByUserIdAndCompleted(userId: Long, completed: Boolean): List<TaskEntity>

    // 3. Devuelve las tareas ordenadas por puntos de mayor a menor
    @Query("SELECT t FROM TaskEntity t WHERE t.user.id = :userId ORDER BY t.points DESC")
    fun findTasksByUserIdOrderByPointsDesc(@Param("userId") userId: Long): List<TaskEntity>
}