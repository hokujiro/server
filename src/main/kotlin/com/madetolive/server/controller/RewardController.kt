package com.madetolive.server.controller

import com.madetolive.server.entity.RewardEntity
import com.madetolive.server.entity.UserEntity
import com.madetolive.server.model.*
import com.madetolive.server.repository.ProjectRepository
import com.madetolive.server.repository.RewardsRepository
import com.madetolive.server.repository.UserRepository
import com.madetolive.server.server.RewardsService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api/rewards")
class RewardController (
    val userRepository: UserRepository,
    val projectRepository: ProjectRepository,
    val rewardsRepository: RewardsRepository,
    val rewardsService: RewardsService,
) {
    @GetMapping("/all")
    fun getRewardsForCurrentUser(principal: Principal): ResponseEntity<List<RewardDto>> {
        val user = findUser(principal) ?: return ResponseEntity.notFound().build()
        val rewards = rewardsService.getRewardsListByUserId(user.id)
        return ResponseEntity.ok(rewards.map { it.toDto() })
    }

    @PostMapping("/add")
    fun addReward(
        principal: Principal,
        @RequestBody request: CreateRewardRequest
    ): ResponseEntity<RewardDto> {
        val user = findUser(principal) ?: return ResponseEntity.notFound().build()

        val reward = RewardEntity(
            title = request.title,
            points = request.points,
            user = user,
            photo = request.photoUrl,
            reusable = request.reusable,
            icon = request.icon,
            redeemed = request.redeemed
        )

        request.project?.id.takeIf { it?.isNotBlank() == true && it.all { char -> char.isDigit() } }?.toLongOrNull()?.let { projectId ->
            val project = projectRepository.findById(projectId).orElse(null)
            reward.project = project
        }


        request.bundle?.id.takeIf { it?.isNotBlank() == true && it.all { char -> char.isDigit() } }?.toLongOrNull()?.let { bundleId ->
            val project = projectRepository.findById(bundleId).orElse(null)
            reward.project = project
        }

        val savedReward = rewardsService.addRewardForUser(user, reward)
        return ResponseEntity.ok(savedReward.toDto())
    }

    @PostMapping("/redeem/{id}")
    fun redeemReward(
        principal: Principal,
        @PathVariable id: Long
    ): ResponseEntity<String> {
        val user = findUser(principal) ?: return ResponseEntity.status(401).body("User not found")
        val reward = rewardsRepository.findById(id).orElse(null)
            ?: return ResponseEntity.notFound().build()

        if (reward.user.id != user.id) {
            return ResponseEntity.status(403).body("You are not authorized to redeem this reward")
        }

        if (!reward.reusable && reward.redeemed) {
            return ResponseEntity.badRequest().body("This reward has already been redeemed")
        }

        if (user.getTotalPoints() < reward.points) {
            return ResponseEntity.badRequest().body("Not enough points to redeem this reward")
        }

        // Subtract points
        user.subtractPoints(reward.points)

        // Mark as redeemed if not reusable
        if (!reward.reusable) {
            reward.redeemed = true
        }

        // Save updates
        userRepository.save(user)
        rewardsRepository.save(reward)

        return ResponseEntity.ok("Reward redeemed successfully")
    }

    @PutMapping("/update/{id}")
    fun updateReward(
        principal: Principal,
        @PathVariable id: Long,
        @RequestBody request: CreateRewardRequest
    ): ResponseEntity<RewardDto> {
        val user = findUser(principal) ?: return ResponseEntity.status(401).build()

        val updatedReward = rewardsService.updateRewardForUser(
            user,
            id,
            request
        )
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(updatedReward.toDto())
    }

    @DeleteMapping("/delete/{id}")
    fun deleteReward(
        principal: Principal,
        @PathVariable id: Long
    ): ResponseEntity<Void> {
        val user = findUser(principal) ?: return ResponseEntity.status(401).build()

        val deleted = rewardsService.deleteRewardForUser(user, id)
        return if (deleted) ResponseEntity.noContent().build()
        else ResponseEntity.notFound().build()
    }

    private fun findUser(principal: Principal): UserEntity? {
        val username = principal.name
        return userRepository.findByUsername(username)
    }

}