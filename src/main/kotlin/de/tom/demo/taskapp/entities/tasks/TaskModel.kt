package de.tom.demo.taskapp.entities.tasks

import de.tom.demo.taskapp.entities.Project
import de.tom.demo.taskapp.entities.User
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation
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