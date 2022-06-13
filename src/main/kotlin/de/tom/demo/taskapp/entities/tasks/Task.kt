package de.tom.demo.taskapp.entities.tasks

import de.tom.demo.taskapp.entities.Project
import de.tom.demo.taskapp.entities.User
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate
import java.time.LocalDateTime

@Document(collection = "tasks")
data class Task(
    @Id val id: String?,
    val text: String,
    val description: String?,
    val day: LocalDate,
    val reminder: Boolean,
    val state: String,
    val labels: List<String>,
    val assignees: List<User>,
    val reportedBy: User,
    val consistOf: Project? = null,
    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @LastModifiedDate
    val updatedAt: LocalDateTime? = null
)

