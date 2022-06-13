package de.tom.demo.taskapp.entities

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate
import java.time.LocalDateTime

@Document(collection = "users")
data class User(
    @Id val id: String?,
    val name: String,           // user's full name
    @Indexed(unique=true)
    val username: String,       // user's nickname (unique)
    val password: String,
    @Indexed(unique=true)
    val email: String,          // user's email address (unique)
    val roles: List<String>,
    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @LastModifiedDate
    val updatedAt: LocalDateTime? = null
)

@Document(collection = "projects")
data class Project(
    @Id val id: String?,
    val name: String,
    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now(),
    @LastModifiedDate
    val updatedAt: LocalDateTime? = null
)
