package de.tom.demo.taskapp.entities.tasks

import de.tom.demo.taskapp.Constants
import de.tom.demo.taskapp.TaskNotValidException
import de.tom.demo.taskapp.entities.Task
import de.tom.demo.taskapp.entities.TaskForm
import de.tom.demo.taskapp.entities.projects.ProjectService
import de.tom.demo.taskapp.entities.users.UserService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.format.DateTimeParseException

/**
 * REST Controller for the tasks resource with GET, POST, PUT and DELETE methods.
 */
//  @CrossOrigin(origins = ["http://localhost:3000"])
@RestController
@RequestMapping(Constants.PATH_TASKS)
class TaskController(val service: TaskService, val userService: UserService, val projectService: ProjectService) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    /**
     * Get all tasks of the logged-in user from the database.
     * GET http://localhost:8080/api/tasks/
     */
    @GetMapping(path = ["/"])
    @ResponseStatus(HttpStatus.OK)
    fun getAll(): List<Task> = service.getTasks(userService.getLoggedInUser())

    /**
     * Get the tasks with the id of the logged-in user from the database.
     * GET http://localhost:8080/api/tasks/{id}
     */
    @GetMapping(path = ["/{id}"])
    @ResponseStatus(HttpStatus.OK)
    fun getById(@PathVariable id: String): Task = service.getTaskOfUser(id, userService.getLoggedInUser())

    /**
     * Add a new task to the database and assign it to the project of the given project id.
     * The task is reported by the logged-in user.
     * POST http://localhost:8080/api/tasks/
     */
    @PostMapping(path = ["/"])
    @ResponseStatus(HttpStatus.CREATED)
    fun post(@RequestBody body: TaskForm): Task =
        if (body.text.isEmpty())
            throw TaskNotValidException("add task fields: text, day, reminder")
        else {
            val reportedBy = userService.getLoggedInUser()
            val project = projectService.getProjectByName(body.projectName)
            service.addTask(body.text, body.description, convertStringToLocalDate(body.day), body.reminder,
                project, reportedBy, null)
        }

    /**
     * Remove a tasks with id from the database.
     * DELETE http://localhost:8080/api/tasks/{id}
     */
    @DeleteMapping(path = ["/{id}"])
    @ResponseStatus(HttpStatus.OK)
    fun delete(@PathVariable id: String): Unit = service.deleteTaskOfUser(id, userService.getLoggedInUser())

    /**
     * Update the content of a task with the id.
     * PUT http://localhost:88080/api/tasks/{id}?text={text}&day={day}&reminder={reminder}
     */
    @PutMapping(path = ["/{id}"])
    @ResponseStatus(HttpStatus.OK)
    fun put(@PathVariable id: String, @RequestBody body: TaskForm): Task =
        service.updateTask(id, body.text, convertStringToLocalDate(body.day), body.reminder, userService.getLoggedInUser())

    private fun convertStringToLocalDate(value: String): LocalDate {
        try {
            return LocalDate.parse(value)
        } catch (e: DateTimeParseException) {
            throw TaskNotValidException("Parameter day has wrong format")
        }
    }
}
