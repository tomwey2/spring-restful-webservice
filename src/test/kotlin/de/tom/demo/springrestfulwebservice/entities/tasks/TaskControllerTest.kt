package de.tom.demo.springrestfulwebservice.entities.tasks

import de.tom.demo.springrestfulwebservice.entities.Task
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerIntegrationTest(@Autowired val client: TestRestTemplate) {

    @BeforeAll
    fun setup() {
    }

    @Test
    fun getOneTask() {
        val entity = client.getForEntity<String>("/tasks")

        //val entity = client.getForEntity<String>("/tasks/5056d005-f2d5-4394-b878-b44f37402626")
        Assertions.assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
    }

}