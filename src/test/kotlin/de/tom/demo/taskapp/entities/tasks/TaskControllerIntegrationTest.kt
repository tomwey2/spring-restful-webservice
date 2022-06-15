package de.tom.demo.taskapp.entities.tasks

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import de.tom.demo.taskapp.Constants
import de.tom.demo.taskapp.config.DataConfiguration
import de.tom.demo.taskapp.entities.LoginResponseMessage
import de.tom.demo.taskapp.entities.TaskForm
import de.tom.demo.taskapp.entities.User
import io.mockk.InternalPlatformDsl.toStr
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.*
import org.springframework.test.context.ContextConfiguration
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap


/**
 * Integration tests of the TaskController
 * using the TestRestTemplate of the Spring Boot Test Framework
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration
class TaskControllerIntegrationTest(@Autowired val client: TestRestTemplate,
                                    @LocalServerPort val port: Int, @Autowired val objectMapper: ObjectMapper,
                                    @Autowired val service: TaskService) {
    private val johnDoe = DataConfiguration().johnDoe
    private val janeDoe = DataConfiguration().janeDoe
    private val admin = DataConfiguration().admin


    /**
     * Login a test user to get the LoginResponseMessage. This JSON object contains the access token
     * for authorization of the following requests.
     */
    private fun loginTestUser(user: User): LoginResponseMessage {
        val url = "${Constants.URI_LOCALHOST}:${port}${Constants.PATH_LOGIN}"
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        val params: MultiValueMap<String, String> = LinkedMultiValueMap()
        params.add("email", user.email)
        params.add("password", user.password)

        val request = HttpEntity<MultiValueMap<String, String>>(params, headers)
        val response: ResponseEntity<String> = client.postForEntity(url, request, String::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val result: LoginResponseMessage = objectMapper.readValue(response.body.toStr())
        return result
    }

    private fun getAuthorizationHeader(accessToken: String): MultiValueMap<String, String> {
        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
        headers.add("Content-Type", "application/json")
        headers.add("Authorization", "Bearer $accessToken")
        return headers
    }

    private fun getAllTasksOfUser(loginResult: LoginResponseMessage): List<Task> {
        val url = "${Constants.URI_LOCALHOST}:${port}${Constants.PATH_TASKS}/"
        val headers = getAuthorizationHeader(loginResult.accessToken)
        val response = client.exchange(url, HttpMethod.GET, HttpEntity<Any>(headers), String::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val result: List<Task> = objectMapper.readValue(response.body.toStr())
        return result
    }

    private fun getOneTaskOfUser(loginResult: LoginResponseMessage, id: String): Task {
        val url = "${Constants.URI_LOCALHOST}:${port}${Constants.PATH_TASKS}/${id}/"
        val headers = getAuthorizationHeader(loginResult.accessToken)
        val response = client.exchange(url, HttpMethod.GET, HttpEntity<Any>(headers), String::class.java)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        val result: Task = objectMapper.readValue(response.body.toStr())
        return result
    }

    @Test
    fun `Integration Test - Get all Tasks of user with role ROLE_USER`() {
        val loginResult: LoginResponseMessage = loginTestUser(johnDoe)
        val result: List<Task> = getAllTasksOfUser(loginResult)
        assertThat(result).isNotEmpty
    }

    @Test
    fun `Integration Test - Get all Tasks of user with role ROLE_ADMIN`() {
        val loginResult: LoginResponseMessage = loginTestUser(admin)
        val result: List<Task> = getAllTasksOfUser(loginResult)
        assertThat(result).isNotEmpty
    }

    @Test
    fun `Integration Test - Get one Tasks by id of user with role ROLE_USER`() {
        val loginResult: LoginResponseMessage = loginTestUser(johnDoe)
        // request all tasks of user and select one randomly
        val testTask: Task = getAllTasksOfUser(loginResult).random()
        // request the task with the id of the selected task and check the result
        val result: Task = getOneTaskOfUser(loginResult, testTask.id!!)
        assertThat(result).isEqualTo(testTask)
    }

    @Test
    fun `Integration Test - Add a task of user with role ROLE_USER`() {
        val updatedText = "New Task"
        val updatedDay = "2022-03-01"
        val updatedReminder = true

        val loginResult: LoginResponseMessage = loginTestUser(johnDoe)
        val initialTaskList: List<Task> = service.getAllTasksOfUser(johnDoe)

        // prepare and send the request
        val url = "${Constants.URI_LOCALHOST}:${port}${Constants.PATH_TASKS}/"
        val body = TaskForm(updatedText, null, updatedDay, updatedReminder, Constants.TASK_OPEN)
        val request = HttpEntity(body, getAuthorizationHeader(loginResult.accessToken))
        val response: ResponseEntity<String> = client.postForEntity(url, request, String::class.java)

        // check the response
        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        val result: Task = objectMapper.readValue(response.body.toStr())
        assertThat(result.id).isNotEmpty
        assertThat(result.text).isEqualTo("New Task")
        val updatedTaskList: List<Task> = service.getAllTasksOfUser(johnDoe)
        assertThat(updatedTaskList.size).isEqualTo(initialTaskList.size + 1)
    }

    @Test
    fun `Integration Test - Delete a task with existing id of user with role ROLE_USER`() {
        val loginResult: LoginResponseMessage = loginTestUser(johnDoe)
        // request all tasks of user and select one randomly
        val testTask: Task = getAllTasksOfUser(loginResult).random()
        val initialTaskList: List<Task> = service.getAllTasksOfUser(johnDoe)

        // prepare and send the request
        val url = "${Constants.URI_LOCALHOST}:${port}${Constants.PATH_TASKS}/${testTask.id}"
        val deleteRequest = HttpEntity<String>(getAuthorizationHeader(loginResult.accessToken))
        val deleteResponse: ResponseEntity<String> = client.exchange(url, HttpMethod.DELETE, deleteRequest, String::class.java)

        // check the response
        assertThat(deleteResponse.statusCode).isEqualTo(HttpStatus.OK)
        val updatedTaskList: List<Task> = service.getAllTasksOfUser(johnDoe)
        assertThat(updatedTaskList.size).isEqualTo(initialTaskList.size - 1)

        val getRequest = HttpEntity<String>(getAuthorizationHeader(loginResult.accessToken))
        val getResponse = client.exchange(url, HttpMethod.GET, getRequest, String::class.java)
        assertThat(getResponse.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `Integration Test - Update a task that exist`() {
        val updatedText = "Updated Task"
        val updatedDay = "2022-03-01"
        val updatedReminder = false

        val loginResult: LoginResponseMessage = loginTestUser(johnDoe)
        // request all tasks of user and select one randomly
        val testTask: Task = getAllTasksOfUser(loginResult).random()

        // prepare and send the request
        val url = "http://localhost:${port}${Constants.PATH_TASKS}/${testTask.id}"
        val body = TaskForm(updatedText, null, updatedDay, updatedReminder, Constants.TASK_OPEN)
        val request = HttpEntity(body, getAuthorizationHeader(loginResult.accessToken))
        val response: ResponseEntity<String> = client.exchange(url, HttpMethod.PUT, request, String::class.java)

        // check the response
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        val result: Task = objectMapper.readValue(response.body.toStr())
        assertThat(result.id).isEqualTo(testTask.id)
        assertThat(result.text).isEqualTo("Updated Task")

    }
}