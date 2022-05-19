package de.tom.demo.taskapp.entities.tasks

import de.tom.demo.taskapp.Constants
import de.tom.demo.taskapp.TaskNotFoundException
import de.tom.demo.taskapp.entities.Project
import de.tom.demo.taskapp.entities.Task
import de.tom.demo.taskapp.entities.User
import de.tom.demo.taskapp.entities.users.UserService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate

/**
 * Tasks service class with business functions to add, update and delete a tasks.
 */
@Service
class TaskService(val db: TaskRepository, val userService: UserService) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    /**
     * Gets all tasks which are either reported by or assigned to the user.
     * If the user has the ADMIN role then gets all tasks.
     */
    fun getTasks(user: User): List<Task> {
        //val user = userService.getLoggedInUser()
        return if (user.roles.contains(Constants.ROLE_ADMIN)) db.findAll() else db.findAllUserTasks(user.email)
    }

    /**
     * Gets all tasks that are reported by the user.
     * The admin gets all tasks.
     */
    fun getAllTasksReportedByUser(user: User): List<Task> {
        //val user = userService.getLoggedInUser()
        return if (user.roles.contains(Constants.ROLE_ADMIN)) db.findAll() else db.findAllTasksReportedByUser(user.email)
    }

    /**
     * Gets all tasks that are assigned to the user.
     * The admin gets all tasks.
     */
    fun getAllTasksAssignedToUser(user: User): List<Task> {
        //val user = userService.getLoggedInUser()
        return if (user.roles.contains(Constants.ROLE_ADMIN)) db.findAll() else db.findAllTasksAssignedToUser(user.email)
    }

    /**
     * Get the task of the user with a given id.
     */
    fun getTaskOfUser(id: String, user: User): Task {
        val tasks = getAllTasksReportedByUser(user)
        return tasks.firstOrNull { task: Task -> task.id.equals(id) } ?: throw TaskNotFoundException(id)
    }

    /**
     * Add a new task and assign it to a project. The task must have a user that reported the task.
     * It can have optionally a user who is assigned to it.
     */
    fun addTask(text: String, description: String?, day: LocalDate, reminder: Boolean,
                reportedBy: User, assignedTo: User?): Task {
        val assignees = if (assignedTo != null) listOf(assignedTo) else listOf()
        val newTask = Task(null, text, description, day, reminder,
            Constants.TASK_OPEN, listOf(), assignees, reportedBy)
        return db.save(newTask)
    }

    /**
     * Assign a user to a task. This update the list of assignees of a task.
     */
    fun assignedUserToTask(task: Task, user: User): Task =
        db.save(task.copy(assignees = task.assignees.plus(user)))

    /**
     * Change the list of users that are assigned to the task with id
     * (i.e. replace the old list of users with the new one).
     */
    fun changeAssignedUsers(task: Task, assignees: List<User>): Task =
        db.save(task.copy(assignees = assignees))

    /**
     * Delete a user task that have the given id.
     */
    fun deleteTaskOfUser(id: String, user: User): Unit {
        val task: Task = getTaskOfUser(id, user)
        db.delete(task)
    }

    /**
     * Delete all tasks.
     * TODO: delete only own tasks
     */
    fun deleteAll(): Unit {
        db.deleteAll()
    }

    /**
     * Update the content of a task. That can be the text, day or the reminder flag.
     * TODO: add the description field
     */
    fun updateTask(id: String, text: String, day: LocalDate, reminder: Boolean, user: User): Task {
        val updatedTask = getTaskOfUser(id, user).copy(text = text, day = day, reminder = reminder)
        return db.save(updatedTask)
    }

}
