package de.tom.demo.taskapp.entities.tasks

import de.tom.demo.taskapp.Constants
import de.tom.demo.taskapp.TaskNotFoundException
import de.tom.demo.taskapp.entities.Task
import de.tom.demo.taskapp.entities.User
import de.tom.demo.taskapp.entities.users.UserService
import org.springframework.stereotype.Service
import java.time.LocalDate

/**
 * Tasks service class with business functions to add, update and delete a tasks.
 */
@Service
class TaskService(val db: TaskRepository, val userService: UserService) {

    /**
     * Gets all tasks which are either reported by or assigned to the logged-in user.
     * If the user has the ADMIN role then gets all tasks.
     */
    fun getTasks(user: User): List<Task> {
        //val user = userService.getLoggedInUser()
        return if (user.role == Constants.ROLE_ADMIN) db.findAll() else db.findAllUserTasks(user.email)
    }

    /**
     * Gets all tasks which are reported by the logged-in user.
     * The admin gets all tasks.
     */
    fun getAllTasksReportedByUser(user: User): List<Task> {
        //val user = userService.getLoggedInUser()
        return if (user.role == Constants.ROLE_ADMIN) db.findAll() else db.findAllTasksReportedByUser(user.email)
    }

    /**
     * Gets all tasks which are assigned to the logged-in user.
     * The admin gets all tasks.
     */
    fun getAllTasksAssignedToUser(user: User): List<Task> {
        //val user = userService.getLoggedInUser()
        return if (user.role == Constants.ROLE_ADMIN) db.findAll() else db.findAllTasksAssignedToUser(user.email)
    }

    fun getTaskOfUser(id: String, user: User): Task {
        val tasks = getAllTasksReportedByUser(user)
        return tasks.firstOrNull { task: Task -> task.id.equals(id) } ?: throw TaskNotFoundException(id)
    }

    fun addTask(task: Task): Task = db.save(task)

    fun deleteTaskOfUser(id: String, user: User): Unit {
        val task: Task = getTaskOfUser(id, user)
        db.delete(task)
    }

    fun updateTask(id: String, text: String, day: LocalDate, reminder: Boolean, user: User): Task {
        val updatedTask = getTaskOfUser(id, user).copy(text = text, day = day, reminder = reminder)
        return db.save(updatedTask)
    }

}
