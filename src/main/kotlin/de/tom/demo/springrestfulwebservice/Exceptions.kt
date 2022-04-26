package de.tom.demo.springrestfulwebservice

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import kotlin.RuntimeException

/**
 * Exception handler for task not found. It responds the 404 NOT FOUND status with
 * the message, which task (id) was not found.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
class TaskNotFoundException(id : String) : RuntimeException("Task not found: $id")

/**
 * Exception handler when the task is null or some fields not set. It responds the
 * 400 BAD REQUEST status with a message.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class TaskNotValidException(error : String) : RuntimeException(error)


@ResponseStatus(value = HttpStatus.NOT_FOUND)
class UserNotFoundException(id: String) : RuntimeException("User not found: $id")

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class UserAlreadyExistException(email: String) : RuntimeException("User already exist: $email")

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class CredentialsNotValidException(error : String) : RuntimeException(error)

