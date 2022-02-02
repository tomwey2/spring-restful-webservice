package de.tom.demo.springrestfulwebservice

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class TaskNotFoundException(val id : String) : RuntimeException("Task not found: $id")

