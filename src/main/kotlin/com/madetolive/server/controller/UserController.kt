package com.madetolive.server.controller

import com.madetolive.server.entity.UserEntity
import com.madetolive.server.repository.UserRepository
import com.madetolive.server.server.TaskService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/api/user")
class UserController(
    val taskService: TaskService,
    val userRepository: UserRepository,
) {

    @GetMapping("/points")
    fun getTotalPoints(principal: Principal): ResponseEntity<Float> {
        val user = findUser(principal) ?: return ResponseEntity.status(401).build()
        return ResponseEntity.ok(user.getTotalPoints())
    }

    private fun findUser(principal: Principal): UserEntity? {
        val username = principal.name
        return userRepository.findByUsername(username)
    }
}