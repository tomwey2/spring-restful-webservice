package de.tom.demo.springrestfulwebservice

import de.tom.demo.springrestfulwebservice.entities.users.UserService
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class Application {
	@Bean
	fun init(userService: UserService) = CommandLineRunner {
		userService.registerUser("John Doe", "john.doe@test.com", "1234")
		userService.registerUser("Jane Doe", "jane.doe@test.com", "1234")
		userService.registerUser("Admin", "admin@test.com", "1234", "ROLE_ADMIN")
	}
}

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}


