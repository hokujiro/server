package com.madetolive.server.repository

import com.madetolive.server.entity.ProjectEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ProjectRepository : JpaRepository<ProjectEntity, Long> {
    fun findByUserId(userId: Long): List<ProjectEntity>
}