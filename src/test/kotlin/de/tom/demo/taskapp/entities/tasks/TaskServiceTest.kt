package de.tom.demo.taskapp.entities.tasks

import de.tom.demo.taskapp.Constants
import de.tom.demo.taskapp.TaskNotFoundException
import de.tom.demo.taskapp.config.DataConfiguration
import de.tom.demo.taskapp.entities.users.UserService
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ContextConfiguration
import java.time.LocalDate

/**
 * Testing the TaskService with JUnit5 and MockK.
 * Mocking the TaskRepository
 * Using the AssertJ instead of JUnit (jupiter) assertions because AssertJ is easy to use and more readable.
 */

// no SpringBootTest annotations in order to execute the test functions fast and without the framework
// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockKExtension::class)
@ContextConfiguration
class TaskServiceTest {
    private val repository = mockk<TaskRepository>()
    private val userService = mockk<UserService>()
    private val underTest = TaskService(repository, userService)

    private val johnDoe = DataConfiguration().johnDoe
    private val janeDoe = DataConfiguration().janeDoe
    private val admin = DataConfiguration().admin

    private val idNotExist = "5678"
    private val testTask = Task("4711", "New Task", "",
        LocalDate.now(), true, Constants.TASK_CLOSED, listOf(),
        listOf(johnDoe), johnDoe, DataConfiguration().project)

    @BeforeEach
    fun setup() {
        println(">> Mocking the repository calls with test data")

        every { repository.findAllUserTasks(johnDoe.email) } returns DataConfiguration().getAllTasksOfUser(johnDoe)
        every { repository.findAllUserTasks(janeDoe.email) } returns DataConfiguration().getAllTasksOfUser(janeDoe)
        every { repository.findAllUserTasks(admin.email) } returns DataConfiguration().getAllTasksOfUser(admin)
        every { repository.findAll() } returns DataConfiguration().getAllTasksOfUser(admin)

        every { repository.findAllTasksReportedByUser(johnDoe.email) } returns DataConfiguration().getAllTestTasksReportedByUser(johnDoe)
        every { repository.findAllTasksReportedByUser(janeDoe.email) } returns DataConfiguration().getAllTestTasksReportedByUser(janeDoe)
        every { repository.findAllTasksReportedByUser(admin.email) } returns DataConfiguration().getAllTestTasksReportedByUser(admin)

        every { repository.findAllTasksAssignedToUser(johnDoe.email) } returns DataConfiguration().getAllTestTasksAssignedToUser(johnDoe)
        every { repository.findAllTasksAssignedToUser(janeDoe.email) } returns DataConfiguration().getAllTestTasksAssignedToUser(janeDoe)
        every { repository.findAllTasksAssignedToUser(admin.email) } returns DataConfiguration().getAllTestTasksAssignedToUser(admin)

        every { repository.delete(any()) } returns Unit
        every { repository.save(any()) } returns testTask

    }

    @Test
    fun `Get all Tasks of user with role ROLE_USER`() {
        val tasks = underTest.getTasks(johnDoe)
        assertThat(tasks).hasSameSizeAs(DataConfiguration().getAllTasksOfUser(johnDoe))
    }

    @Test
    fun `Get all Tasks of user with role ROLE_ADMIN`() {
        val result = underTest.getTasks(admin)
        assertThat(result).hasSameSizeAs(DataConfiguration().getAllTasksOfUser(admin))
    }

    @Test
    fun `Get one Tasks by id of user with role ROLE_USER`() {
        val testTask = DataConfiguration().getOneRandomTestTaskReportedByUser(johnDoe)
        val result = underTest.getTaskOfUser(testTask.id!!, johnDoe)
        assertThat(result).usingComparator(TaskTestUtils.taskComparator).isEqualTo(testTask)
    }

    @Test
    fun `Failed to get one Tasks by id of user with role ROLE_ADMIN`() {
        assertThrows( TaskNotFoundException::class.java) { underTest.getTaskOfUser("", admin) }
        assertThrows( TaskNotFoundException::class.java) { underTest.getTaskOfUser(idNotExist, admin) }
    }

    @Test
    fun `Add a task of user with role ROLE_USER`() {
        val newTask = underTest.addTask(
            testTask.text, testTask.description, testTask.day, testTask.reminder, johnDoe, janeDoe)
        assertEquals(testTask, newTask)
    }

    @Test
    fun `Delete a task with existing id of user with role ROLE_USER`() {
        val testTask = DataConfiguration().getOneRandomTestTaskReportedByUser(johnDoe)
        val johnsTasks = DataConfiguration().getAllTestTasksReportedByUser(johnDoe)
        every { repository.findAllTasksReportedByUser(johnDoe.email) } returns johnsTasks
//        justRun { underTest.deleteTaskOfUser(testTask.id!!, johnDoe) }
        underTest.deleteTaskOfUser(testTask.id!!, johnDoe)
    }

    @Test
    fun `Failed to delete a task with not existing id of user with role ROLE_USER`() {
        assertThrows( TaskNotFoundException::class.java) { underTest.deleteTaskOfUser("", johnDoe) }
        assertThrows( TaskNotFoundException::class.java) { underTest.deleteTaskOfUser(idNotExist, johnDoe) }
    }

    @Test
    fun `Update a task that exist`() {
        val testTask = DataConfiguration().getOneRandomTestTaskReportedByUser(johnDoe)
        val savedTask = testTask.copy(text = "updated", day = testTask.day, reminder = testTask.reminder)
        every { repository.save(any()) } returns savedTask

        val result = underTest.updateTask(testTask.id!!, "updated", null, testTask.day, testTask.reminder,
            testTask.state, johnDoe)
        assertThat(result.text).isEqualTo(savedTask.text)
    }

    @Test
    fun `Failed tp update a task which does not exist`() {
        assertThrows( TaskNotFoundException::class.java) {
            underTest.updateTask(idNotExist, testTask.text, null, testTask.day, testTask.reminder,
                testTask.state, johnDoe) }
    }

}