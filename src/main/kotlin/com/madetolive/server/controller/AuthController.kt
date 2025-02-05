package com.madetolive.server.controller

import com.madetolive.server.config.GoogleTokenVerifier
import com.madetolive.server.config.JwtService
import com.madetolive.server.entity.UserEntity
import com.madetolive.server.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
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
    fun register(@RequestBody user: UserEntity): ResponseEntity<String> {
        if (userRepository.findByUsername(user.username).isPresent) {
            return ResponseEntity.badRequest().body("Username is already taken")
        }

        val hashedUser = user.copy(password = passwordEncoder.encode(user.password))
        userRepository.save(hashedUser)
        return ResponseEntity.ok("User registered successfully")
    }

    @PostMapping("/login")
    fun login(@RequestBody authRequest: AuthRequest): ResponseEntity<Map<String, String>> {
        // Autenticar al usuario
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(authRequest.username, authRequest.password)
        )

        // Generar JWT
        val userDetails = authentication.principal as UserEntity
        val jwt = jwtService.generateToken(userDetails)

        // Devolver el JWT al cliente
        return ResponseEntity.ok(mapOf("token" to jwt))
    }

    @PostMapping("/google-login")
    fun googleLogin(@RequestBody request: GoogleAuthRequest): ResponseEntity<AuthResponse> {
        val verifiedUser = GoogleTokenVerifier.verify(request.idToken)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null)

        // Check if user exists in database
        val user = userRepository.findByGoogleId(verifiedUser.googleId)
            ?: userRepository.findByEmail(verifiedUser.email) // Check email fallback
            ?: userRepository.save(
                UserEntity(
                    username = verifiedUser.name,
                    email = verifiedUser.email,
                    googleId = verifiedUser.googleId
                )
            )

        // Generate JWT token for authentication
        val jwtToken = jwtService.generateToken(user)

        return ResponseEntity.ok(

            (user.id, user.username, user.email, jwtToken))
    }

    data class GoogleAuthRequest(val idToken: String)
    data class AuthRequest(val username: String, val password: String)
    data class AuthResponse(val userId: Long, val name: String, val email: String, val token: String)

}