package com.madetolive.server.config

import com.madetolive.server.server.UserService
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Esta clase extrae el username que llega desde el JWT en el authorization header, lo valida,
 * recoge los userDetails del usuario llamando al servicio (UserService) y crea con él un
 * authentication object
 */
@Component
class JwtRequestFilter(
    private val jwtUtil: JwtService,  // Utility to validate and parse JWT
    private val userService: UserService // To load user details by username
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // Extract the "Authorization" header
        val authorizationHeader = request.getHeader("Authorization")

        // Check if the header contains a Bearer token
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            val jwtToken = authorizationHeader.substring(7)

            try {
                // Validate and extract username from the token
                val username = jwtUtil.extractUsername(jwtToken)

                // Check if the user is not already authenticated
                if (username != null && SecurityContextHolder.getContext().authentication == null) {

                    // Load user details
                    val userDetails: UserDetails = userService.loadUserByUsername(username)

                    // Validate token with the username
                    if (jwtUtil.validateToken(jwtToken, userDetails.username)) {
                        // Create an authentication object with user details
                        val authentication = UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.authorities
                        )
                        // Set the authentication in the SecurityContext
                        SecurityContextHolder.getContext().authentication = authentication
                    }
                }
            } catch (e: JwtException) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT Token")
                return
            }
        }
        // Continue the filter chain
        filterChain.doFilter(request, response)
    }
}