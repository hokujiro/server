package com.madetolive.server.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "supabase")
class SupabaseConfig {
    lateinit var url: String
    lateinit var serviceKey: String
}