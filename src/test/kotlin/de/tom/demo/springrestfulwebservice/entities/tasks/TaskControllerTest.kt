package de.tom.demo.springrestfulwebservice.entities.tasks

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import de.tom.demo.springrestfulwebservice.TaskNotFoundException
import de.tom.demo.springrestfulwebservice.config.DataConfiguration
import org.junit.jupiter.api.Test
import de.tom.demo.springrestfulwebservice.entities.Task
import io.mockk.InternalPlatformDsl.toStr
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

import java.time.LocalDateTime
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockKExtension::class)
class TaskControllerTest(@Autowired val mockMvc: MockMvc, @Autowired val objectMapper: ObjectMapper) {
    val idNotExist = "5678"
    var testData= listOf<Task>()
    val testTask = Task(null, "New Task",
        LocalDateTime.of(2022, 2, 24, 18, 0), true)

    // Mocking the TaskService
    @TestConfiguration
    class ControllerTestConfig {
        @Bean
        fun service() = mockk<TaskService>()
    }
    @Autowired
    lateinit var service: TaskService

    @BeforeAll
    fun setUp() {
        // build up the test data from configuration.
        DataConfiguration().testTasks.map {
            val uid = UUID.randomUUID().toStr()
            testData = testData.plus(it.copy(id = uid))
        }

        testData.map {
            val id = it.id ?: ""
            every { service.getTask(id) } returns it
            every { service.updateTask(id, testTask) } returns
                    it.copy(text = testTask.text, day = testTask.day, reminder = testTask.reminder)
        }
        val idExist = testData[0].id ?: ""
        every { service.getTasks() } returns testData
        every { service.getTask(idNotExist) } throws TaskNotFoundException(idNotExist)
        every { service.addTask(testTask) } returns testTask.copy(id = UUID.randomUUID().toStr())
        every { service.deleteTask(idExist)} returns Unit
        every { service.deleteTask(idNotExist)} throws TaskNotFoundException(idNotExist)
        every { service.updateTask(idNotExist, any()) } throws TaskNotFoundException(idNotExist)
    }

    @Test
    @DisplayName("test GET /tasks")
    fun getAll() {
        val json = mockMvc.perform(get("/tasks/"))
            //.andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk)
            .andReturn()
            .response.contentAsString

        val result: List<Task> = objectMapper.readValue(json)
        Assertions.assertThat(result).hasSize(testData.size)
        result.map {
            Assertions.assertThat(it).isIn(testData)
        }

    }

    @Test
    @DisplayName("test GET /tasks/{id} with existing id")
    fun getById() {
        val idExist = testData[0].id
        val json = mockMvc.perform(get("/tasks/${idExist}"))
            .andExpect(status().isOk)
            .andReturn()

        println(json.response.contentAsString)
        verify { service.getTask(testData[0].id ?: "") }
    }

    @Test
    @DisplayName("test GET /tasks/{id} with id that not exist")
    fun failedGetById() {
        val result = mockMvc.perform(get("/tasks/${idNotExist}"))
            //.andDo(MockMvcResultHandlers.print())
            .andExpect(status().isNotFound)
            .andReturn()

        verify { service.getTask(idNotExist) }
    }

    @Test
    @DisplayName("test POST /tasks/")
    fun post() {
        val json = mockMvc.perform(post("/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testTask)))
            //.andDo(MockMvcResultHandlers.print())
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
            .response.contentAsString

        val result: Task = objectMapper.readValue(json)
        Assertions.assertThat(result.id).isNotEmpty
    }

    @Test
    @DisplayName("Test the REST API: DELETE /tasks/{id}")
    fun delete() {
        val idExist = testData[0].id
        mockMvc.perform(
            delete("/tasks/${idExist}"))
            //.andDo(MockMvcResultHandlers.print())
            .andExpect(status().is2xxSuccessful)
            .andReturn()
    }

    @Test
    @DisplayName("test DELETE /tasks/{id} with id that not exists")
    fun failedDelete() {

        mockMvc.perform(
            delete("/tasks/${idNotExist}"))
            //.andDo(MockMvcResultHandlers.print())
            .andExpect(status().isNotFound)

    }

    @Test
    @DisplayName("Test PUT /tasks/{id}")
    fun put() {
        Assertions.assertThat(testData[0].text).isNotEqualTo(testTask.text)

        val idExist = testData[0].id
        val json = mockMvc.perform(
            put("/tasks/${idExist}")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testTask)))
            //.andDo(MockMvcResultHandlers.print())
            .andExpect(status().is2xxSuccessful)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
            .response.contentAsString

        val result: Task = objectMapper.readValue(json)
        Assertions.assertThat(result.id).isEqualTo(idExist)
        Assertions.assertThat(result.text).isEqualTo(testTask.text)
    }

    @Test
    @DisplayName("test PUT /tasks/{id} with id that not exists")
    fun failedPut() {
        Assertions.assertThat(testData[0].text).isNotEqualTo(testTask.text)

        mockMvc.perform(
            put("/tasks/${idNotExist}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTask)))
            //.andDo(MockMvcResultHandlers.print())
            .andExpect(status().isNotFound)

    }

}
