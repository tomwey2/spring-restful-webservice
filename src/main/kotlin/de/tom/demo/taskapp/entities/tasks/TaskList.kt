package de.tom.demo.taskapp.entities.tasks

data class TaskList(
    val query: String?,
    val open: Int,
    val closed: Int,
    val entities: List<TaskModel>
)
