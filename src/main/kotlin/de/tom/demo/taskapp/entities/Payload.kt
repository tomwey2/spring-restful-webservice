package de.tom.demo.taskapp.entities

import de.tom.demo.taskapp.entities.tasks.Task

data class LoginForm(val username: String, val password: String)

data class RegisterForm(val name: String, val username: String, val email: String, val password: String)

data class TaskForm(val text: String, val description: String?, val day: String, val reminder: Boolean)
data class ResponseMessage(val message: String, val text: String?)

data class LoginResponseMessage(val username: String, val roles: List<String>, val accessToken: String, val refreshToken: String)

data class RefreshTokenRequestMessage(val refreshToken: String)

data class RefreshTokenResponseMessage(val accessToken: String, val refreshToken: String)

data class TasksQueryResult(val open: Int, val closed: Int, val tasks: List<Task>)

