package de.tom.demo.taskapp.entities.tasks

import de.tom.demo.taskapp.Constants
import de.tom.demo.taskapp.entities.Task
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import de.tom.demo.taskapp.TaskNotValidException
import de.tom.demo.taskapp.entities.projects.ProjectService
import de.tom.demo.taskapp.entities.users.UserService
import java.time.LocalDate

/**
 * REST Controller with GET, POST, PUT and DELETE methods.
 */
//  @CrossOrigin(origins = ["http://localhost:3000"])
@RestController
@RequestMapping("/api/tasks")
class TaskController(val service: TaskService, val userService: UserService, val projectService: ProjectService) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    // GET method to get all tasks from database
    // http://localhost:8080/api/tasks/
    @GetMapping(path = ["/"])
    @ResponseStatus(HttpStatus.OK)
    fun getAll(): List<Task> = service.getTasks()

    // GET task details based on ‘Id’
    // http://localhost:8080/api/tasks/{id}
    @GetMapping(path = ["/{id}"])
    @ResponseStatus(HttpStatus.OK)
    fun getById(@PathVariable id: String): Task = service.getTask(id)

    // POST a new task
    // http://localhost:8080/api/tasks/
    @PostMapping(path = ["/"])
    @ResponseStatus(HttpStatus.CREATED)
    fun post(@RequestParam text: String, @RequestParam description: String?,
             @RequestParam day: String, @RequestParam reminder: Boolean,
             @RequestParam reportedByEmail: String, @RequestParam projectName: String): Task =
        if (text.isEmpty())
            throw TaskNotValidException("add task fields: text, day, reminder")
        else {
            log.info("post: $text, $description, $day, $reminder, $reportedByEmail, $projectName")
            val dayDate = LocalDate.parse(day) // TODO: check day
            val reportedBy = userService.getUserByEmail(reportedByEmail)
            val project = projectService.getProjectByName(projectName)
            val task = Task(null, text, description, dayDate, reminder, Constants.TASK_CREATED, null, listOf(), reportedBy, project)
            service.addTask(task)
        }

    // Remove/Delete task by id
    // http://localhost:8080/api/tasks/{id}
    @DeleteMapping(path = ["/{id}"])
    @ResponseStatus(HttpStatus.OK)
    fun delete(@PathVariable id: String): Unit = service.deleteTask(id)

    // PUT — Update task details by id
    // http://localhost:88080/api/tasks/{id}
    @PutMapping(path = ["/{id}"])
    @ResponseStatus(HttpStatus.OK)
    fun put(@PathVariable id: String,
            @RequestParam text: String, @RequestParam day: String, @RequestParam reminder: Boolean): Task {
        val dayDate = LocalDate.parse(day) // TODO: check day
        return service.updateTask(id, text, dayDate, reminder)
    }
}
