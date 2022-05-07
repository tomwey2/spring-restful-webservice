package de.tom.demo.taskapp.entities.tasks

import de.tom.demo.taskapp.TaskNotValidException
import java.time.LocalDate
import java.time.format.DateTimeParseException

object TaskUtils {

    fun convertStringToLocalDate(value: String): LocalDate {
        try {
            return LocalDate.parse(value)
        } catch (e: DateTimeParseException) {
            throw TaskNotValidException("Parameter day has wrong format")
        }
    }
}