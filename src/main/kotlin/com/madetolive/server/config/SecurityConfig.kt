package com.madetolive.server.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

/**
 * Esta clase tiene los métodos para hacer hash de la password introducida,
 * crear la cadena de filtros de autenticación y crear el autenticationManager.
 * Hace que los endpoints de tipo /api/auth/... estén abiertos y el resto requieran autenticación
 */
@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtRequestFilter: JwtRequestFilter // Inject your JwtRequestFilter
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        jwtRequestFilter: JwtRequestFilter
    ): SecurityFilterChain {
        return http
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers("/api/auth/**").permitAll()
                it.requestMatchers("/api/auth/refresh").permitAll()
                it.requestMatchers("/error").permitAll()
                it.requestMatchers("/api/tasks/**").authenticated()
                it.requestMatchers("/api/user/**").authenticated()
                it.requestMatchers("/api/projects/**").authenticated()
                it.requestMatchers("/api/frames/**").authenticated()
                it.requestMatchers("/api/rewards/**").authenticated()
                it.anyRequest().denyAll()
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build() // ✅ Only call build ONCE, at the end
    }

    @Bean
    fun authenticationManager(authConfig: AuthenticationConfiguration) =
        authConfig.authenticationManager
}