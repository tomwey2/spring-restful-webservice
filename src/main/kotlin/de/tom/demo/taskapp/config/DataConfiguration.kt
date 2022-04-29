package de.tom.demo.taskapp.config

import de.tom.demo.taskapp.Constants
import de.tom.demo.taskapp.entities.Project
import de.tom.demo.taskapp.entities.Task
import de.tom.demo.taskapp.entities.User
import de.tom.demo.taskapp.entities.projects.ProjectRepository
import de.tom.demo.taskapp.entities.tasks.TaskRepository
import de.tom.demo.taskapp.entities.users.UserRepository
import de.tom.demo.taskapp.entities.users.UserService
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDate

@Configuration
class DataConfiguration {
    final val project = Project(null, "p1")
    final val johnDoe = User(null, "John Doe", "1234", "john.doe@test.com", Constants.ROLE_USER)
    final val janeDoe = User(null, "Jane Doe", "1234", "jane.doe@test.com", Constants.ROLE_USER)
    final val admin = User(null,"Admin", "1234", "admin@test.com", Constants.ROLE_ADMIN)

    final val task1 = Task(null, "Food shopping", "",
            LocalDate.now().plusDays(10), true,
            Constants.TASK_OPEN, null, listOf(johnDoe), johnDoe, project)
    final val task2 = Task(null, "Doctor appointment", "",
            LocalDate.now().plusDays(5), true,
        Constants.TASK_OPEN, null, listOf(johnDoe, janeDoe), johnDoe, project)
    final val task3 = Task(null, "School party preparation", "",
            LocalDate.now().minusDays(2), true,
        Constants.TASK_CLOSED, null, listOf(janeDoe), johnDoe, project)
    final val task4 = Task(null, "Backup databases", "",
            LocalDate.now(), false,
        Constants.TASK_OPEN, null, listOf(admin), admin, project)
    val testTasks = listOf(task1, task2, task3, task4)

    @Bean
    fun databaseInitializer(
        userService: UserService,
        userRepository: UserRepository,
        taskRepository: TaskRepository,
        projectRepository: ProjectRepository) = ApplicationRunner {

        projectRepository.deleteAll()
        taskRepository.deleteAll()
        userRepository.deleteAll()

        val dbProject = projectRepository.save(project)

        val dbJohnDoe = userService.registerUser(johnDoe.name, johnDoe.email, johnDoe.password)
        val dbJaneDoe = userService.registerUser(janeDoe.name, janeDoe.email, janeDoe.password)
        val dbAdmin = userService.registerUser(admin.name, admin.email, admin.password, Constants.ROLE_ADMIN)

        val dbTask1 = task1.copy(assignees = listOf(dbJohnDoe), reportedBy = dbJohnDoe, consistOf = dbProject)
        val dbTask2 = task2.copy(assignees = listOf(dbJohnDoe, dbJaneDoe), reportedBy = dbJohnDoe, consistOf = dbProject)
        val dbTask3 = task3.copy(assignees = listOf(dbJaneDoe), reportedBy = dbJohnDoe, consistOf = dbProject)
        val dbTask4 = task4.copy(assignees = listOf(dbAdmin), reportedBy = dbAdmin, consistOf = dbProject)

        listOf(dbTask1, dbTask2, dbTask3, dbTask4).map {
            taskRepository.save(it)
        }
    }

}

