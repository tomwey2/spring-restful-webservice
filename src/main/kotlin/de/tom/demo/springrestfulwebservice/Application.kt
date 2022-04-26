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
		userService.registerUser(name="abc", email = "abc@bcd", password = "1234")
	}
}

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}


