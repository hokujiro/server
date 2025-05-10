package com.madetolive.server.server

import com.madetolive.server.controller.RewardController
import com.madetolive.server.entity.FrameEntity
import com.madetolive.server.entity.RewardEntity
import com.madetolive.server.entity.UserEntity
import com.madetolive.server.model.DailyPointsSummary
import com.madetolive.server.repository.*
import org.springframework.stereotype.Service
import com.madetolive.server.model.CreateRewardRequest
import java.time.LocalDate
import java.util.*

@Service
class RewardsService(
    private val userRepository: UserRepository,
    private val projectRepository: ProjectRepository,
    private val rewardsRepository: RewardsRepository,
) {

    fun getRewardsListByUserId(userId: Long): List<RewardEntity> {
        return rewardsRepository.findByUserId(userId)
    }

    fun addRewardForUser(user: UserEntity, reward: RewardEntity): RewardEntity {
        reward.user = user
        return rewardsRepository.save(reward)
    }

    fun updateRewardForUser(
        user: UserEntity,
        rewardId: Long,
        request: CreateRewardRequest
    ): RewardEntity? {
        val existing = rewardsRepository.findById(rewardId).orElse(null) ?: return null
        if (existing.user.id != user.id) return null

        // 🔍 Find the ProjectEntity using projectId from request.project.id
        val projectEntity = request.project.id.toLongOrNull()?.let { projectId ->
            projectRepository.findById(projectId).orElse(null)
        }

        val updated = existing.copy(
            title = request.title,
            points = request.points,
            project = projectEntity,
        )

        val saved = rewardsRepository.save(updated)

        userRepository.save(user)
        return saved
    }

    fun deleteRewardForUser(user: UserEntity, rewardId: Long): Boolean {
        val reward = rewardsRepository.findById(rewardId).orElse(null) ?: return false
        if (reward.user.id != user.id) return false

        rewardsRepository.delete(reward)
        return true
    }

    fun addRewardsForUser(user: UserEntity, rewards: List<RewardEntity>): List<RewardEntity> {
        return rewardsRepository.saveAll(rewards)
    }

    fun deleteRewardsForUser(user: UserEntity, rewardIds: List<Long>): Boolean {
        val rewards = rewardsRepository.findAllById(rewardIds)
        val userOwnsAll = rewards.all { it?.user?.id == user.id }
        if (!userOwnsAll) return false

        rewardsRepository.deleteAll(rewards)
        return true
    }

    fun getRewardsByUserIdSortedByPoints(userId: Long): List<RewardEntity> {
        return rewardsRepository.findRewardsByUserIdOrderByPointsDesc(userId)
    }

}