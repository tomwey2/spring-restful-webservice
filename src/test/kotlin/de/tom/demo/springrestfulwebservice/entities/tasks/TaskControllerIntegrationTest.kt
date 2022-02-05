package de.tom.demo.springrestfulwebservice.entities.tasks

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import de.tom.demo.springrestfulwebservice.entities.Task
import io.mockk.InternalPlatformDsl.toStr
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.*
import kotlin.random.Random


/**
 * Integration tests of the TaskController
 * using the TestRestTemplate of the Spring Boot Test Framework
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerIntegrationTest(@Autowired val client: TestRestTemplate,
    @LocalServerPort val port: Int, @Autowired val objectMapper: ObjectMapper,
                                    @Autowired val service: TaskService) {

    @BeforeAll
    fun setUp() {
        val list: List<Task> = service.getTasks()
        list.map { println(it) }
    }

    @AfterAll
    fun setDown() {
        val list: List<Task> = service.getTasks()
        list.map { println(it) }
    }

    @Test
    @DisplayName("integration test for GET /tasks")
    fun getAll() {
        val initialTasks: List<Task> = service.getTasks()
        val url = "http://localhost:${port}/tasks"

        val response = client.getForEntity<String>(url)
        val result: List<Task> = objectMapper.readValue(response.body.toStr())

        Assertions.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        Assertions.assertThat(result).hasSize(initialTasks.size)
    }

    @Test
    @DisplayName("integration test for GET /tasks/{id}")
    fun getById() {
        val initialTasks: List<Task> = service.getTasks()
        val testTask: Task = initialTasks[Random.nextInt(0, initialTasks.size)]
        val url = "http://localhost:${port}/tasks/${testTask.id}"

        val response = client.getForEntity<String>(url)
        val result: Task = objectMapper.readValue(response.body.toStr())

        Assertions.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        Assertions.assertThat(result.text).isEqualTo(testTask.text)
    }

    @Test
    @DisplayName("integration test for POST /tasks/")
    fun post() {
        val initialTasks: List<Task> = service.getTasks()
        val url = "http://localhost:${port}/tasks"

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val json = """{"text": "New Task", "day": "2022-03-01 12:45:00.000.00.0", "reminder": true}"""
        val request = HttpEntity<String>(json, headers)

        val response: ResponseEntity<String> = client.postForEntity(url, request, String::class.java)
        val result: Task = objectMapper.readValue(response.body.toStr())
        val updatedTasks: List<Task> = service.getTasks()

        Assertions.assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        Assertions.assertThat(result.id).isNotEmpty()
        Assertions.assertThat(result.text).isEqualTo("New Task")
        Assertions.assertThat(updatedTasks.size).isEqualTo(initialTasks.size + 1)
    }

    @Test
    @DisplayName("integration test for DELETE /tasks/{id}")
    fun delete() {
        val initialTasks: List<Task> = service.getTasks()
        val testTask: Task = initialTasks[Random.nextInt(0, initialTasks.size)]
        val url = "http://localhost:${port}/tasks/${testTask.id}"

        client.delete(url)
        val updatedTasks: List<Task> = service.getTasks()

        Assertions.assertThat(updatedTasks.size).isEqualTo(initialTasks.size - 1)
        val entity = client.getForEntity<String>(url)
        Assertions.assertThat(entity.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    @DisplayName("integration test for PUT /tasks/")
    fun put() {
        val initialTasks: List<Task> = service.getTasks()
        val testTask: Task = initialTasks[Random.nextInt(0, initialTasks.size)]
        val url = "http://localhost:${port}/tasks/${testTask.id}"

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val requestBody = """{"text": "Updated Task", "day": "2022-03-01 12:45:00.000.00.0", "reminder": false}"""

        val request = HttpEntity<String>(requestBody, headers)
        val response: ResponseEntity<String> = client.exchange(url, HttpMethod.PUT, request, String::class.java)

        Assertions.assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val result: Task = objectMapper.readValue(response.body.toStr())
        Assertions.assertThat(result.id).isEqualTo(testTask.id)
        Assertions.assertThat(result.text).isEqualTo("Updated Task")

    }
}