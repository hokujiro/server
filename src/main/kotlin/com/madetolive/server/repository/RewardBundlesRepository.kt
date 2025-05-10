package com.madetolive.server.repository

import com.madetolive.server.entity.RewardBundleEntity
import com.madetolive.server.entity.RewardEntity
import com.madetolive.server.entity.TaskEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate


interface RewardBundlesRepository : JpaRepository<RewardBundleEntity?, Long?> {

    // 1. Devolver las tareas por userId
    fun findByUserId(userId: Long): List<RewardBundleEntity>
}