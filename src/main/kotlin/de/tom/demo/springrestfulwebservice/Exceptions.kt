package de.tom.demo.springrestfulwebservice

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import kotlin.RuntimeException

/**
 * Exception handler for task not found. It responds the 404 NOT FOUND status with
 * the message, which task (id) was not found.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
class TaskNotFoundException(private val id : String) : RuntimeException("Task not found: $id")

/**
 * Exception handler when the task is null or some fields not set. It responds the
 * 400 BAD REQUEST status with a message.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class TaskNotValidException(private val error : String) : RuntimeException(error)


@ResponseStatus(value = HttpStatus.NOT_FOUND)
class UserNotFoundException(private val id: String) : RuntimeException("User not found: $id")