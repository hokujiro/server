package com.madetolive.server.repository

import com.madetolive.server.entity.Bonus
import org.springframework.data.jpa.repository.JpaRepository

interface BonusRepository : JpaRepository<Bonus, Long> {
    // Custom methods as needed
}