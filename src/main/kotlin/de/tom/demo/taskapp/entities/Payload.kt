package de.tom.demo.taskapp.entities

data class LoginForm(val email: String, val password: String)
data class RegisterForm(val name: String, val email: String, val password: String)

data class ResponseMessage(val message: String, val text: String?)
data class LoginResponseMessage(val email: String, val roles: List<String>, val accessToken: String, val refreshToken: String)

