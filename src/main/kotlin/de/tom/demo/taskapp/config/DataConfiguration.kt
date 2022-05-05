package de.tom.demo.taskapp.config

import de.tom.demo.taskapp.Constants
import de.tom.demo.taskapp.entities.Project
import de.tom.demo.taskapp.entities.Task
import de.tom.demo.taskapp.entities.User
import de.tom.demo.taskapp.entities.projects.ProjectRepository
import de.tom.demo.taskapp.entities.tasks.TaskRepository
import de.tom.demo.taskapp.entities.tasks.TaskService
import de.tom.demo.taskapp.entities.users.UserRepository
import de.tom.demo.taskapp.entities.users.UserService
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDate

@Configuration
class DataConfiguration {
    final val project = Project("p1", "p1")
    final val johnDoe = User("john", "John Doe", "1234", "john.doe@test.com", listOf(Constants.ROLE_USER))
    final val janeDoe = User("jane", "Jane Doe", "1234", "jane.doe@test.com", listOf(Constants.ROLE_USER))
    final val admin = User("admin", "Admin", "1234", "admin@test.com", listOf(Constants.ROLE_ADMIN, Constants.ROLE_USER))

    final val testTasks = listOf(
        Task("t1",
            "Food shopping",
            "One time in week food must be bought.",
            LocalDate.now().plusDays(10),
            true,
            Constants.TASK_OPEN,
            listOf(), listOf(johnDoe),
            johnDoe,
            project
        ),
        Task("t2",
            "Doctor appointment",
            "Meet the doctor to ask him about new set of medicament",
            LocalDate.now().plusDays(5),
            true,
            Constants.TASK_OPEN,
            listOf(),
            listOf(johnDoe, janeDoe),
            johnDoe,
            project
        ),
        Task("t3",
            "School party preparation",
            "",
            LocalDate.now().minusDays(2),
            true,
            Constants.TASK_CLOSED,
            listOf(),
            listOf(janeDoe),
            johnDoe,
            project),
        Task("t4",
            "Backup databases",
            "",
            LocalDate.now(),
            false,
            Constants.TASK_OPEN,
            listOf(),
            listOf(admin),
            admin,
            project)
    )

    fun getAllTasksOfUser(user: User): List<Task> =
        if (user.roles.contains(Constants.ROLE_ADMIN)) {
            testTasks
        } else {
            testTasks.filter { it.reportedBy.email == user.email || it.assignees.contains(user)}
        }

    fun getAllTestTasksReportedByUser(user: User): List<Task> =
        if (user.roles.contains(Constants.ROLE_ADMIN)) {
            testTasks
        } else {
            testTasks.filter { it.reportedBy.email == user.email }
        }

    fun getAllTestTasksAssignedToUser(user: User): List<Task> =
        if (user.roles.contains(Constants.ROLE_ADMIN)) {
            testTasks
        } else {
            testTasks.filter { it.assignees.contains(user) }
        }

    fun getOneRandomTestTaskReportedByUser(user: User): Task =
        getAllTestTasksReportedByUser(user).random()

    @Bean
    fun databaseInitializer(
        userService: UserService,
        userRepository: UserRepository,
        taskService: TaskService,
        projectRepository: ProjectRepository
    ) = ApplicationRunner {

        projectRepository.deleteAll()
        taskService.deleteAll()
        userRepository.deleteAll()

        val dbProject = projectRepository.save(project.copy(id = null))

        val userMap = mapOf(
            johnDoe to userService.registerUser(johnDoe.name, johnDoe.email, johnDoe.password),
            janeDoe to userService.registerUser(janeDoe.name, janeDoe.email, janeDoe.password),
            admin to userService.registerUser(admin.name, admin.email, admin.password, listOf(Constants.ROLE_ADMIN, Constants.ROLE_USER))
        )

        testTasks.map { task: Task ->
            val reportedBy: User = userMap[task.reportedBy]!!
            var newTask = taskService.addTask(task.text, task.description, task.day, task.reminder, dbProject, reportedBy, null)
            task.assignees.map {user: User ->
                val assignee: User = userMap[user]!!
                newTask = taskService.assignedUserToTask(newTask, assignee)
            }
        }
    }

}

