package com.madetolive.server.repository

import com.madetolive.server.entity.BundleEntity
import org.springframework.data.jpa.repository.JpaRepository


interface RewardBundlesRepository : JpaRepository<BundleEntity?, Long?> {

    // 1. Devolver las tareas por userId
    fun findByUserId(userId: Long): List<BundleEntity>
}