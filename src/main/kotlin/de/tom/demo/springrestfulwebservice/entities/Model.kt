package de.tom.demo.springrestfulwebservice.entities

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("TASKS")
data class Task(
    @Id val id: String?,
    val text: String,
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss[.SSS][.SS][.S]")
    val day: LocalDateTime,
    val reminder: Boolean
)

@Table("USERS")
data class User(
    @Id val id: String?,
    val name: String,
    val email: String,
    @JsonIgnore
    val password: String,
    val role: String
)
