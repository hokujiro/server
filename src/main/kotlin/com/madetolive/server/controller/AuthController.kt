package com.madetolive.server.controller

import com.madetolive.server.config.GoogleTokenVerifier
import com.madetolive.server.config.JwtService
import com.madetolive.server.entity.UserEntity
import com.madetolive.server.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService,  // Utility to validate and parse JWT
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @PostMapping("/register")
    fun register(@RequestBody user: UserEntity): ResponseEntity<Any>{
        if (userRepository.findByUsername(user.username)!=null) {
            return ResponseEntity.badRequest().body(ErrorResponse("Username is already taken"))
        }

        val hashedUser = user.copy(password = passwordEncoder.encode(user.password))
        userRepository.save(hashedUser)
        val jwtToken = jwtService.generateToken(user)
        return ResponseEntity.ok(
            AuthResponse(
                userId = user.id,
                name = user.username,
                email = user.email,
                token = jwtToken
            )
        )
    }

    @PostMapping("/login")
    fun login(@RequestBody authRequest: AuthRequest): ResponseEntity<AuthResponse> {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(authRequest.username, authRequest.password)
        )

        val springUser = authentication.principal as org.springframework.security.core.userdetails.User
        val userEntity = userRepository.findByUsername(springUser.username)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        val jwtToken = jwtService.generateToken(userEntity)

        return ResponseEntity.ok(
            AuthResponse(
                userId = userEntity.id,
                name = userEntity.username,
                email = userEntity.email,
                token = jwtToken
            )
        )
    }

    data class GoogleAuthRequest(val idToken: String)
    data class AuthRequest(val username: String, val password: String)
    data class AuthResponse(val userId: Long, val name: String, val email: String, val token: String)
    data class ErrorResponse(val message: String)

}