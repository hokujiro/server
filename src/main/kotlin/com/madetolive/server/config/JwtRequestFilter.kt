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
        val path = request.servletPath

// ✅ Skip all /api/auth endpoints from filtering
        if (path.startsWith("/api/auth")) {
            println("✅ Skipping JWT filter for path: $path")
            filterChain.doFilter(request, response)
            return
        }

        val authorizationHeader = request.getHeader("Authorization")

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            val jwtToken = authorizationHeader.substring(7)

            try {
                val username = jwtUtil.extractUsername(jwtToken)

                if (username != null && SecurityContextHolder.getContext().authentication == null) {
                    val userDetails = userService.loadUserByUsername(username)

                    if (jwtUtil.validateToken(jwtToken, userDetails.username)) {
                        val authentication = UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.authorities
                        )
                        authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                        SecurityContextHolder.getContext().authentication = authentication
                    } else {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT Token")
                        return
                    }
                }

            } catch (e: Exception) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Error: ${e.message}")
                return
            }
        } else {
            println("ℹ️ JWT Filter: No Authorization header or doesn't start with Bearer")
        }

        filterChain.doFilter(request, response)
    }
}