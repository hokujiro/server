package com.madetolive.server.repository

import com.madetolive.server.entity.PointTransactionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface PointTransactionRepository : JpaRepository<PointTransactionEntity, Long> {

    fun findAllByUserId(userId: Long): List<PointTransactionEntity>

    @Query("SELECT SUM(p.points) FROM PointTransactionEntity p WHERE p.user.id = :userId")
    fun sumPointsByUserId(userId: Long): Float?
}