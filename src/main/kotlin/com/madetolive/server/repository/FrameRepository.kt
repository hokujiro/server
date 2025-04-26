package com.madetolive.server.repository

import com.madetolive.server.entity.FrameEntity
import org.springframework.data.jpa.repository.JpaRepository

interface FrameRepository : JpaRepository<FrameEntity?, Long?> {
    fun findByUserId(userId: Long): List<FrameEntity>
}