package de.tom.demo.taskapp.entities

import org.springframework.data.annotation.Id
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
    val label: String?,
    val assignees: List<User>,
    val reportedBy: User,
    val consistOf: Project,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime? = null
)

@Document(collection = "users")
data class User(
    @Id val id: String?,
    val name: String,
    val password: String,
    val email: String,
    val roles: List<String>,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime? = null
)

@Document(collection = "projects")
data class Project(
    @Id val id: String?,
    val name: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime? = null
)
