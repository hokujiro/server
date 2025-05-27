package com.madetolive.server.controller

import com.madetolive.server.SupabaseStorageService
import com.madetolive.server.entity.UserEntity
import com.madetolive.server.model.UserDto
import com.madetolive.server.repository.UserRepository
import com.madetolive.server.server.TaskService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.security.Principal
import java.util.*

@RestController
@RequestMapping("/api/user")
class UserController(
    val taskService: TaskService,
    val userRepository: UserRepository,
    val storage: SupabaseStorageService,
) {

    @GetMapping("/points")
    fun getTotalPoints(principal: Principal): ResponseEntity<Float> {
        val user = findUser(principal) ?: return ResponseEntity.status(401).build()
        return ResponseEntity.ok(user.getTotalPoints())
    }

    @GetMapping("/me")
    fun getCurrentUser(principal: Principal): ResponseEntity<UserDto> {
        val user = findUser(principal) ?: return ResponseEntity.status(401).build()
        return ResponseEntity.ok(UserDto.fromEntity(user))
    }

    @PutMapping("/update")
    fun updateUserProfile(
        principal: Principal,
        @RequestBody request: UpdateUserRequest
    ): ResponseEntity<Void> {
        val user = findUser(principal) ?: return ResponseEntity.status(401).build()

        val updated = user.copy(
            username = request.username,
            photo = request.photo
        )
        userRepository.save(updated)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/upload-photo", consumes = ["multipart/form-data"])
    fun uploadPhoto(
        principal: Principal,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<String> {
        val user = findUser(principal) ?: return ResponseEntity.status(401).build()

        // Delete previous images
        storage.deleteAllUserImages(user.id)

        val fileName = "profile_${UUID.randomUUID()}.jpg"
        val contentType = file.contentType ?: "image/jpeg"
        val bytes = file.bytes

        val photoUrl = storage.uploadImage(user.id, fileName, contentType, bytes)

        val updated = user.copy(photo = photoUrl)
        userRepository.save(updated)

        return ResponseEntity.ok(photoUrl)
    }
    private fun findUser(principal: Principal): UserEntity? {
        val username = principal.name
        return userRepository.findByUsername(username)
    }
}

data class UpdateUserRequest(
    val username: String,
    val photo: String
)