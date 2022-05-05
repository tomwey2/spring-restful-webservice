package de.tom.demo.taskapp.entities.tasks

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import de.tom.demo.taskapp.Constants
import de.tom.demo.taskapp.TaskNotFoundException
import de.tom.demo.taskapp.config.DataConfiguration
import de.tom.demo.taskapp.entities.LoginResponseMessage
import de.tom.demo.taskapp.entities.Task
import de.tom.demo.taskapp.entities.User
import de.tom.demo.taskapp.entities.projects.ProjectService
import de.tom.demo.taskapp.entities.users.UserService
import io.mockk.InternalPlatformDsl.toStr
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import java.time.LocalDate
import java.util.*

@SpringBootTest
@ContextConfiguration
@AutoConfigureMockMvc
@ExtendWith(MockKExtension::class)
class TaskControllerTest(@Autowired val mockMvc: MockMvc, @Autowired val objectMapper: ObjectMapper) {
    val idNotExist = "5678"

    private val johnDoe = DataConfiguration().johnDoe
    private val janeDoe = DataConfiguration().janeDoe
    private val admin = DataConfiguration().admin

    // Mocking the TaskService
    @TestConfiguration
    class ControllerTestConfig {
        @Bean
        fun service() = mockk<TaskService>()
    }
    @Autowired
    lateinit var service: TaskService
    private val userService = mockk<UserService>()
    private val projectService = mockk<ProjectService>()

    private fun loginTestUser(user: User): LoginResponseMessage {
        val params: MultiValueMap<String, String> = LinkedMultiValueMap()
        params.add("email", user.email)
        params.add("password", user.password)

        val json = mockMvc.perform(
            post("${Constants.PATH_LOGIN}")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(params)
                .accept("application/json;charset=UTF-8")
            )
            .andExpect(status().isOk)
            .andReturn()
            .response.contentAsString

        val result: LoginResponseMessage = objectMapper.readValue(json.toString())
        return result
    }

    @Test
    fun `Get all Tasks of user with role ROLE_USER`() {
        val loginResponse = loginTestUser(johnDoe)

        // mock the services
        every { userService.getLoggedInUser() } returns johnDoe
        every { service.getTasks(any()) } returns DataConfiguration().getAllTasksOfUser(johnDoe)  // hint: any() because logged-in user has another id

        // call the controller with the access token for authorization
        val jsonResponse = mockMvc.perform(
            get("${Constants.PATH_TASKS}/")
                .header("Authorization", "Bearer ${loginResponse.accessToken}"))
            .andExpect(status().isOk)
            .andReturn()
            .response.contentAsString

        val result: List<Task> = objectMapper.readValue(jsonResponse)
        assertThat(result).hasSize(DataConfiguration().getAllTasksOfUser(johnDoe).size)
        result.map {
            assertThat(it)
                .usingComparator(TaskTestUtils.taskComparator)
                .isIn(DataConfiguration().getAllTasksOfUser(johnDoe))
        }

    }

    @Test
    fun `Get one Tasks by id of user with role ROLE_USER`() {
        val loginResponse = loginTestUser(johnDoe)
        val testTask = DataConfiguration().getOneRandomTestTaskReportedByUser(johnDoe)

        // mock the services
        every { userService.getLoggedInUser() } returns johnDoe
        every { service.getTaskOfUser(testTask.id!!, any()) } returns testTask  // hint: any() because logged-in user has another id

        val jsonResponse = mockMvc.perform(
            get("${Constants.PATH_TASKS}/${testTask.id}")
                .header("Authorization", "Bearer ${loginResponse.accessToken}"))
            .andExpect(status().isOk)
            .andReturn()
            .response.contentAsString

        val result: Task = objectMapper.readValue(jsonResponse)

        assertThat(result).isEqualTo(testTask)
    }

    @Test
    fun `Failed to Get one Tasks by id that not exist`() {
        val loginResponse = loginTestUser(johnDoe)

        // mock the services
        every { userService.getLoggedInUser() } returns johnDoe
        every { service.getTaskOfUser(idNotExist, any()) } throws TaskNotFoundException(idNotExist)  // hint: any() because logged-in user has another id

        val result = mockMvc.perform(
            get("${Constants.PATH_TASKS}/$idNotExist")
                .header("Authorization", "Bearer ${loginResponse.accessToken}"))
            .andExpect(status().isNotFound)
            .andReturn()

    }

    @Test
    fun `Add a task of user with role ROLE_USER`() {
        val loginResponse = loginTestUser(johnDoe)
        val newTask = Task("4711", "New Task", null,
            LocalDate.now(), true, Constants.TASK_CREATED, listOf(), listOf(),
            johnDoe, DataConfiguration().project)

        // mock the services
        every { userService.getLoggedInUser() } returns johnDoe
        every { service.addTask(any(), any(), any(), any(), any(), any(), any()) } returns newTask.copy(id = UUID.randomUUID().toStr())

        val params = "text=${newTask.text}&day=${newTask.day}&reminder=${newTask.reminder}&reportedByEmail=${DataConfiguration().johnDoe.email}&projectName=${DataConfiguration().project.name}"
        val json = mockMvc.perform(
            post("${Constants.PATH_TASKS}/?$params")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer ${loginResponse.accessToken}"))
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
            .response.contentAsString

        val result: Task = objectMapper.readValue(json)
        assertThat(result.id).isNotEmpty
    }

    @Test
    fun `Delete a task with existing id of user with role ROLE_USER`() {
        val loginResponse = loginTestUser(johnDoe)
        val testTask = DataConfiguration().getOneRandomTestTaskReportedByUser(johnDoe)

        // mock the services
        every { userService.getLoggedInUser() } returns johnDoe
        every { service.deleteTaskOfUser(testTask.id!!, any())} returns Unit // hint: any() because logged-in user has another id

        mockMvc.perform(
            delete("${Constants.PATH_TASKS}/${testTask.id}")
                .header("Authorization", "Bearer ${loginResponse.accessToken}"))
            .andExpect(status().is2xxSuccessful)
            .andReturn()
    }

    @Test
    fun `Failed to delete a task with not existing id of user with role ROLE_USER`() {
        val loginResponse = loginTestUser(johnDoe)

        // mock the services
        every { userService.getLoggedInUser() } returns johnDoe
        every { service.deleteTaskOfUser(idNotExist, any())} throws TaskNotFoundException(idNotExist)  // hint: any() because logged-in user has another id

        mockMvc.perform(
            delete("${Constants.PATH_TASKS}/${idNotExist}")
                .header("Authorization", "Bearer ${loginResponse.accessToken}"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `Update a task that exist`() {
        val loginResponse = loginTestUser(johnDoe)
        val oldTask = DataConfiguration().getOneRandomTestTaskReportedByUser(johnDoe)
        val newTask = oldTask.copy(id = null,  text = "updated")

        // mock the services
        every { userService.getLoggedInUser() } returns johnDoe
        DataConfiguration().getAllTasksOfUser(johnDoe).map {
            every { service.updateTask(it.id!!, "updated", any(), any(), any()) } returns it.copy(text = "updated")
        }

        assertThat(oldTask.text).isNotEqualTo(newTask.text)
        val params = "text=${newTask.text}&day=${newTask.day}&reminder=${newTask.reminder}"

        val jsonResponse = mockMvc.perform(
            put("${Constants.PATH_TASKS}/${oldTask.id}?$params")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer ${loginResponse.accessToken}")
                .content(objectMapper.writeValueAsString(oldTask)))
            //.andDo(MockMvcResultHandlers.print())
            .andExpect(status().is2xxSuccessful)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
            .response.contentAsString

        val result: Task = objectMapper.readValue(jsonResponse)
        assertThat(result.id).isEqualTo(oldTask.id)
        assertThat(result.text).isEqualTo(newTask.text)
    }

    @Test
    fun `Failed to update a task that not exist`() {
        val loginResponse = loginTestUser(johnDoe)
        val oldTask = DataConfiguration().getOneRandomTestTaskReportedByUser(johnDoe)
        val newTask = oldTask.copy(id = null,  text = "updated")

        // mock the services
        every { userService.getLoggedInUser() } returns johnDoe
        every { service.updateTask(idNotExist, any(), any(), any(), any()) } throws TaskNotFoundException(idNotExist)

        assertThat(oldTask.text).isNotEqualTo(newTask.text)
        val params = "text=${newTask.text}&day=${newTask.day}&reminder=${newTask.reminder}"
        mockMvc.perform(
            put("${Constants.PATH_TASKS}/${idNotExist}?$params")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer ${loginResponse.accessToken}")
                .content(objectMapper.writeValueAsString(newTask)))
            .andExpect(status().isNotFound)

    }

}
