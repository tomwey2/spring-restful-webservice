package de.tom.demo.taskapp.entities.tasks

import de.tom.demo.taskapp.TaskNotFoundException
import de.tom.demo.taskapp.entities.Task
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDate

/**
 * Tasks service class with business functions to add, update and delete a tasks.
 */
@Service
class TaskService(val db: TaskRepository) {

    fun getTasks(): List<Task> = db.findAll()

    fun getTask(id: String): Task = db.findByIdOrNull(id) ?: throw  TaskNotFoundException(id)

    fun addTask(task: Task): Task = db.save(task)

    fun deleteTask(id: String): Unit {
        val task: Task = getTask(id)
        db.delete(task)
    }

    fun updateTask(id: String, text: String, day: LocalDate, reminder: Boolean): Task {
        val updatedTask = getTask(id).copy(text = text, day = day, reminder = reminder)
        return db.save(updatedTask)
    }
}
