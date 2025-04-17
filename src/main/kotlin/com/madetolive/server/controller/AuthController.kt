package com.madetolive.server.controller

import com.auth0.jwt.exceptions.JWTVerificationException
import com.madetolive.server.JwtService
import com.madetolive.server.RefreshTokenRequest
import com.madetolive.server.TokenResponse
import com.madetolive.server.entity.UserEntity
import com.madetolive.server.repository.UserRepository
import com.madetolive.server.server.RefreshTokenService
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
    private val refreshTokenService: RefreshTokenService,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @PostMapping("/register")
    fun register(@RequestBody user: UserEntity): ResponseEntity<Any> {
        // Check if username is already taken
        if (userRepository.findByUsername(user.username) != null) {
            return ResponseEntity.badRequest().body(ErrorResponse("Username is already taken"))
        }

        // Encode the password before saving
        val hashedUser = user.copy(password = passwordEncoder.encode(user.password))
        val savedUser = userRepository.save(hashedUser) // ✅ Save & get generated ID

        // Generate access & refresh tokens
        val jwtToken = jwtService.generateToken(savedUser)
        val refreshToken = refreshTokenService.createRefreshToken(savedUser.id)

        return ResponseEntity.ok(
            AuthResponse(
                userId = savedUser.id,
                name = savedUser.username,
                email = savedUser.email,
                token = jwtToken,
                refreshToken = refreshToken
            )
        )
    }

    @PostMapping("/login")
    fun login(@RequestBody authRequest: AuthRequest): ResponseEntity<AuthResponse> {
        // Authenticate user credentials
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(authRequest.username, authRequest.password)
        )

        // Extract authenticated user details from Spring
        val springUser = authentication.principal as org.springframework.security.core.userdetails.User

        // Lookup your user entity
        val userEntity = userRepository.findByUsername(springUser.username)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        // Generate access token
        val jwtToken = jwtService.generateToken(userEntity)

        // ✅ Generate and store refresh token in DB
        val refreshToken = refreshTokenService.createRefreshToken(userEntity.id)

        return ResponseEntity.ok(
            AuthResponse(
                userId = userEntity.id,
                name = userEntity.username,
                email = userEntity.email,
                token = jwtToken,
                refreshToken = refreshToken
            )
        )
    }

    @PostMapping("/refresh")
    fun refreshToken(@RequestBody request: RefreshTokenRequest): ResponseEntity<TokenResponse> {
        return try {
            val username = jwtService.extractUsername(request.refreshToken)
            val userDetails = userRepository.findByUsername(username)
            println("El token en cuestion ${request.refreshToken}")

            if (userDetails == null || !jwtService.validateToken(request.refreshToken, userDetails.username)) {
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
            } else {
                val newAccessToken = jwtService.generateToken(userDetails)
                val newRefreshToken = jwtService.generateRefreshToken(userDetails.username)
                ResponseEntity.ok(TokenResponse(newAccessToken, newRefreshToken))
            }
        } catch (e: JWTVerificationException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null)
        }
    }


    data class GoogleAuthRequest(val idToken: String)
    data class AuthRequest(val username: String, val password: String)
    data class AuthResponse(
        val userId: Long,
        val name: String,
        val email: String,
        val token: String,
        val refreshToken: String
    )
    data class ErrorResponse(val message: String)

}