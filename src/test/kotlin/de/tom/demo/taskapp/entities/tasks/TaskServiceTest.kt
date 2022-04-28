package de.tom.demo.taskapp.entities.tasks

import de.tom.demo.taskapp.Constants
import de.tom.demo.taskapp.TaskNotFoundException
import de.tom.demo.taskapp.entities.Project
import de.tom.demo.taskapp.entities.Task
import de.tom.demo.taskapp.entities.User
import io.mockk.*
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Testing the TaskService with JUnit5 and MockK.
 * Mocking the TaskRepository
 * Using the AssertJ instead of JUnit (jupiter) assertions because AssertJ is easy to use and more readable.
 */

// no SpringBootTest annotations in order to execute the test functions fast and without the framework
// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockKExtension::class)
class TaskServiceTest {
    val testUser = User("u1", "test", "1234", "test@test.com", Constants.ROLE_USER)
    val testProject = Project("p1", "test")
    val testTask = Task("4711", "New Task", "",
        LocalDate.now(), true, Constants.TASK_CLOSED, null, listOf<User>(testUser), testUser, testProject)

    private val idExist = testTask.id ?: ""
    private val idNotExist = "5678"

    private val repository = mockk<TaskRepository>()
    private val underTest = TaskService(repository)

    @BeforeAll
    fun setup() {
        println(">> Mocking the repository calls with test data")
        every { repository.findAll() } returns listOf()
        every { repository.findByIdOrNull("") } returns null
        every { repository.findByIdOrNull(idNotExist) } returns null
        every { repository.findByIdOrNull(idExist) } returns testTask

        every { repository.save(testTask) } returns testTask
        every { repository.delete(any()) } returns Unit
    }

    @Test
    @DisplayName("Test the getTasks() function")
    fun getTasks() {
        underTest.getTasks()
        verify { underTest.getTasks() }
    }

    @Test
    @DisplayName("Test the getTask(id) with existing id")
    fun getTask() {
        assertEquals(testTask, underTest.getTask(idExist))
    }

    @Test
    @DisplayName("Test the getTask(id) with not existing id")
    fun failedGetTask() {
        assertThrows( TaskNotFoundException::class.java) { underTest.getTask("") }
        assertThrows( TaskNotFoundException::class.java) { underTest.getTask(idNotExist) }
    }

    @Test
    @DisplayName("Test the addTask(task)")
    fun addTask() {
        assertEquals(testTask, underTest.addTask(testTask))
    }

    @Test
    @DisplayName("Test the deleteTask(id) with existing id")
    fun deleteTask() {
        justRun { underTest.deleteTask(idExist) }
    }

    @Test
    @DisplayName("Test the deleteTask(id) with not existing id")
    fun failedDeleteTask() {
        assertThrows( TaskNotFoundException::class.java) { underTest.deleteTask("") }
        assertThrows( TaskNotFoundException::class.java) { underTest.deleteTask(idNotExist) }
    }

    @Test
    @DisplayName("Test the updateTask(id) with existing id")
    fun updateTask() {
        assertEquals(testTask, underTest.updateTask(idExist, testTask.text, testTask.day, testTask.reminder))
    }

    @Test
    @DisplayName("Test the updateTask(id) with not existing id")
    fun failedUpdateTask() {
        assertThrows( TaskNotFoundException::class.java) {
            underTest.updateTask(idNotExist, testTask.text, testTask.day, testTask.reminder) }
    }

}