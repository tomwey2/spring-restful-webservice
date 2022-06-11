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

    final val johnDoe = User("john", "John Doe", "John", "1234", "john.doe@test.com",
        listOf(Constants.ROLE_USER))

    final val janeDoe = User("jane", "Jane Doe", "Jane", "1234", "jane.doe@test.com",
        listOf(Constants.ROLE_USER))

    final val admin = User("admin", "Administrator", "Admin", "1234", "admin@test.com",
        listOf(Constants.ROLE_ADMIN, Constants.ROLE_USER))

    final val bret = User("1", "Leanne Graham", "Bret","1234", "Sincere@april.biz",
        listOf(Constants.ROLE_USER))

    final val antonette = User("2", "Ervin Howell", "Antonette", "1234", "Shanna@melissa.tv",
        listOf(Constants.ROLE_USER))

    final val samantha = User("3","Clementine Bauch", "Samantha", "1234", "Nathan@yesenia.net",
        listOf(Constants.ROLE_USER))

    final val karianne = User("4", "Patricia Lebsack", "Karianne", "1234", "Julianne.OConner@kory.org",
        listOf(Constants.ROLE_USER))

    final val kamren = User("5", "Chelsey Dietrich", "Kamren", "1234", "Lucio_Hettinger@annie.ca",
        listOf(Constants.ROLE_USER))

    final val leopoldo = User("6", "Mrs. Dennis Schulist", "Leopoldo_Corkery", "1234", "Karley_Dach@jasper.info",
        listOf(Constants.ROLE_USER))

    final val elwyn = User("7", "Kurtis Weissnat", "Elwyn.Skiles", "1234", "Telly.Hoeger@billy.biz",
        listOf(Constants.ROLE_USER))

    final val maxime = User("8", "Nicholas Runolfsdottir V", "Maxime_Nienow", "1234", "Sherwood@rosamond.me",
        listOf(Constants.ROLE_USER))

    final val delphine = User("9", "Glenna Reichert", "Delphine", "1234", "Chaim_McDermott@dana.io",
        listOf(Constants.ROLE_USER))

    final val moriah = User("10", "Clementina DuBuque", "Moriah.Stanton", "1234", "Rey.Padberg@karina.biz",
        listOf(Constants.ROLE_USER))

    final val testTasks = listOf(
        Task("t1",
            "Food shopping",
            "One time in week food must be bought.",
            LocalDate.now().plusDays(10),
            true,
            Constants.TASK_OPEN,
            listOf(),
            listOf(johnDoe, bret),
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
            listOf(johnDoe, janeDoe, elwyn),
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
            listOf(janeDoe, bret, johnDoe),
            delphine,
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
            johnDoe to userService.registerUser(johnDoe.name, johnDoe.username, johnDoe.email, johnDoe.password),
            janeDoe to userService.registerUser(janeDoe.name, janeDoe.username, janeDoe.email, janeDoe.password),
            bret to userService.registerUser(bret.name, bret.username, bret.email, bret.password),
            delphine to userService.registerUser(delphine.name, delphine.username, delphine.email, delphine.password),
            elwyn to userService.registerUser(elwyn.name, elwyn.username, elwyn.email, elwyn.password),
            admin to userService.registerUser(admin.name, admin.username, admin.email, admin.password, listOf(Constants.ROLE_ADMIN, Constants.ROLE_USER))
        )

        testTasks.map { task: Task ->
            val reportedBy: User = userMap[task.reportedBy]!!
            var newTask = taskService.addTask(task.text, task.description, task.day, task.reminder, reportedBy, null)
            if (task.state == Constants.TASK_CLOSED) {
                newTask = taskService.changeState(newTask.id ?: "", task.state, reportedBy)
            }
            task.assignees.map {user: User ->
                val assignee: User = userMap[user]!!
                newTask = taskService.assignedUserToTask(newTask, assignee)
            }
        }
    }

}

