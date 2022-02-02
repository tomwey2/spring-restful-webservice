package de.tom.demo.springrestfulwebservice.entities.tasks

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import de.tom.demo.springrestfulwebservice.TaskNotFoundException
import org.junit.jupiter.api.Test
import de.tom.demo.springrestfulwebservice.entities.Task
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

import java.time.LocalDateTime

@WebMvcTest(controllers = [TaskController::class])
@ExtendWith(MockKExtension::class)
class TaskControllerTest {
    val idsExist = listOf("1234", "4711", "0815")
    val idsNotExist = listOf("5678")
    val testDay = LocalDateTime.of(2022, 2, 24, 18, 0)

    @TestConfiguration
    class ControllerTestConfig {
        @Bean
        fun service() = mockk<TaskService>()
    }

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var service: TaskService

    @BeforeEach
    fun setUp() {
        var taskList = listOf<Task>()
        idsExist.map {
            val task = Task(it, "Test Tasks ${it}", testDay, true)
            every { service.getTask(it) } returns task
            taskList = taskList.plus(task)
        }
        every { service.getTasks() } returns taskList
        idsNotExist.map { every { service.getTask(it) } throws TaskNotFoundException(it) }
        every { service.addTask(any()) } returns Task("9999", "Task 9999", testDay, true)
    }

    @Test
    @DisplayName("Test the REST API: GET /tasks")
    fun getAll() {
        val json = mockMvc.perform(get("/tasks/"))
            //.andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andReturn()
            .response.contentAsString

        val result: List<Task> = objectMapper.readValue(json)
        Assertions.assertThat(result).hasSize(idsExist.size)
        result.map {
            Assertions.assertThat(it.id).isIn(idsExist)
            Assertions.assertThat(it.text).isEqualTo("Test Tasks ${it.id}")
        }

    }

    @Test
    @DisplayName("Test the REST API: GET /tasks/{id}")
    fun getById() {

        val result = mockMvc.perform(get("/tasks/${idsExist[0]}"))
            .andExpect(status().isOk)
            .andReturn()

        println(result.response.contentAsString)
        verify { service.getTask(idsExist[0]) }
    }

    @Test
    @DisplayName("Fail Test the REST API: GET /tasks/{id}")
    fun failedGetById() {
        idsNotExist.map {
            val result = mockMvc.perform(get("/tasks/${it}"))
                //.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound)
                .andReturn()

            println(result.response.contentAsString)
            verify { service.getTask(it) }
        }
    }

    @Test
    @DisplayName("Test the REST API: POST /tasks/")
    fun post() {

    }

    @Test
    @DisplayName("Test the REST API: DELETE /tasks/{id}")
    fun delete() {

    }

    @Test
    @DisplayName("Test the REST API: PUT /tasks/{id}")
    fun put() {

    }
}
