package com.madetolive.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import io.github.cdimascio.dotenv.dotenv


@SpringBootApplication
@EnableJpaRepositories("com.madetolive.server.repository")
@EntityScan("com.madetolive.server.entity")
class MadeToLiveServerApp

fun main(args: Array<String>) {
	runApplication<MadeToLiveServerApp>(*args)
}
