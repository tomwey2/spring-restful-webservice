package de.tom.demo.springrestfulwebservice.entities.tasks

import de.tom.demo.springrestfulwebservice.entities.Task
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import de.tom.demo.springrestfulwebservice.TaskNotValidException

/**
 * REST Controller with GET, POST, PUT and DELETE methods.
 */
//  @CrossOrigin(origins = ["http://localhost:3000"])
@RestController
@RequestMapping("/api/tasks")
class TaskController(val service: TaskService) {
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
    fun post(@RequestBody task: Task?): Task =
        if (task == null || task.text.isEmpty())
            throw TaskNotValidException("add task fields: text, day, reminder")
        else
            service.addTask(task)

    // Remove/Delete task by id
    // http://localhost:8080/api/tasks/{id}
    @DeleteMapping(path = ["/{id}"])
    @ResponseStatus(HttpStatus.OK)
    fun delete(@PathVariable id: String): Unit = service.deleteTask(id)

    // PUT — Update task details by id
    // http://localhost:88080/api/tasks/{id}
    @PutMapping(path = ["/{id}"])
    @ResponseStatus(HttpStatus.OK)
    fun put(@PathVariable id: String, @RequestBody task: Task): Task = service.updateTask(id, task)
}
