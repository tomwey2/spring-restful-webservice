package de.tom.demo.taskapp.entities.tasks

import de.tom.demo.taskapp.entities.Project
import de.tom.demo.taskapp.entities.User
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

@Relation(collectionRelation = "tasks")
data class TaskModel(
    val id: String,
    val text: String,
    val description: String?,
    val day: LocalDate,
    val reminder: Boolean,
    val state: String,
    val labels: List<String>,
    val assignees: List<User>,
    val reportedBy: User,
    val consistOf: Project? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime? = null
) : RepresentationModel<TaskModel>()

@Component
class TaskModelAssembler : RepresentationModelAssemblerSupport<Task, TaskModel>
    (TaskController::class.java, TaskModel::class.java) {

    override fun toModel(task: Task): TaskModel {
        return this.createModelWithId(task.id.toString(), task)
    }

    override fun instantiateModel(task: Task): TaskModel {
        return TaskModel(
            task.id!!,            // the id cannot be null here!
            task.text,
            task.description,
            task.day,
            task.reminder,
            task.state,
            task.labels,
            task.assignees,
            task.reportedBy,
            task.consistOf,
            task.createdAt,
            task.updatedAt
        )
    }
}

