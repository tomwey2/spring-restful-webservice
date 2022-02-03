package de.tom.demo.springrestfulwebservice.entities.tasks

import de.tom.demo.springrestfulwebservice.entities.Task
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

/**
 * REST Controller with GET, POST, PUT and DELETE methods.
 */
@RestController
class TaskController(val service: TaskService) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    // GET method to get all tasks from database
    // http://localhost:8080/tasks/
    @GetMapping(path = ["/tasks"])
    fun getAll(): List<Task> = service.getTasks()

    // GET task details based on ‘Id’
    // http://localhost:8080/tasks/{id}
    @GetMapping(path = ["/tasks/{id}"])
    fun getById(@PathVariable id: String): Task {
        log.info("get task with id=$id")
        return service.getTask(id)
    }

    // POST a new task
    // http://localhost:8080/tasks/
    @PostMapping(path = ["/tasks"])
    @ResponseStatus(HttpStatus.CREATED)
    fun post(@RequestBody task: Task) : Task {
        log.info("add task: $task")
        return service.addTask(task)
    }

    // Remove/Delete task by id
    // http://localhost:8080/tasks/{id}
    @DeleteMapping(path = ["/tasks/{id}"])
    fun delete(@PathVariable id: String) {
        log.info("delete task with id=$id")
        service.deleteTask(id)
    }

    // PUT — Update task details by id
    // http://localhost:88080/tasks/{id}
    @PutMapping(path = ["/tasks/{id}"])
    fun put(@PathVariable id: String, @RequestBody task: Task) : Task {
        log.info("change task with id=$id new=$task")
        return service.updateTask(id, task)
    }
}
