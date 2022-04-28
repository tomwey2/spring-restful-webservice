package de.tom.demo.taskapp

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ApplicationTests {
	// a simple sanity check test that will fail if the application context cannot start.

	@Test
	fun contextLoads() {
		println("ApplicationTests")
	}

}
