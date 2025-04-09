package com.madetolive.server.repository

import com.madetolive.server.entity.ProjectEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ProjectRepository : JpaRepository<ProjectEntity, Long> {
    fun findByUserId(userId: Long): List<ProjectEntity>
}