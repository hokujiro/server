package com.madetolive.server.config

import com.madetolive.server.JwtService
import com.madetolive.server.server.UserService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Esta clase extrae el username que llega desde el JWT en el authorization header, lo valida,
 * recoge los userDetails del usuario llamando al servicio (UserService) y crea con él un
 * authentication object
 */
@Component
class JwtRequestFilter(
    private val jwtUtil: JwtService,
    private val userService: UserService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authorizationHeader = request.getHeader("Authorization")

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            val jwtToken = authorizationHeader.substring(7)

            try {
                val username = jwtUtil.extractUsername(jwtToken)

                // Only authenticate if no one is already authenticated
                if (username != null && SecurityContextHolder.getContext().authentication == null) {
                    val userDetails = userService.loadUserByUsername(username)

                    if (jwtUtil.validateToken(jwtToken, userDetails.username)) {
                        val authentication = UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.authorities
                        )
                        authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

                        SecurityContextHolder.getContext().authentication = authentication

                        println("✅ JWT Filter: Authenticated $username and set security context")
                    } else {
                        println("❌ JWT Filter: Token validation failed for $username")
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT Token")
                        return
                    }
                }

            } catch (e: com.auth0.jwt.exceptions.TokenExpiredException) {
                println("❌ JWT Filter: Token expired")
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has expired")
                return
            } catch (e: com.auth0.jwt.exceptions.JWTVerificationException) {
                println("❌ JWT Filter: Invalid JWT")
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT Token")
                return
            } catch (e: Exception) {
                println("❌ JWT Filter: Unexpected error: ${e.message}")
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected authentication error")
                return
            }
        } else {
            println("ℹ️ JWT Filter: No Authorization header or doesn't start with Bearer")
        }

        // Proceed with the rest of the filter chain
        filterChain.doFilter(request, response)
    }
}