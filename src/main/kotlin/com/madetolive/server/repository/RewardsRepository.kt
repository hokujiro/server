package com.madetolive.server.repository

import com.madetolive.server.entity.RewardEntity
import com.madetolive.server.entity.TaskEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate


interface RewardsRepository : JpaRepository<RewardEntity?, Long?> {

    // 1. Devolver las rewards por userId
    fun findByUserId(userId: Long): List<RewardEntity>

    // 3. Devuelve las tareas ordenadas por puntos de mayor a menor
    @Query("SELECT t FROM RewardEntity t WHERE t.user.id = :userId ORDER BY t.points DESC")
    fun findRewardsByUserIdOrderByPointsDesc(@Param("userId") userId: Long): List<RewardEntity>

}