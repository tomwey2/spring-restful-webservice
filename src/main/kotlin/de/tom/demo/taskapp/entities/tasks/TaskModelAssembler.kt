package de.tom.demo.taskapp.entities.tasks

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport
import org.springframework.stereotype.Component

@Component
class TaskModelAssembler : RepresentationModelAssemblerSupport<Task, TaskModel>
    (TaskController::class.java, TaskModel::class.java) {

    override fun toModel(task: Task): TaskModel {
        return this.createModelWithId(task.id.toString(), task)
    }

    override fun instantiateModel(task: Task): TaskModel {
        return TaskModel(
            task.id!!,            // the id cannot be null here!
            task.text,
            task.description,
            task.day,
            task.reminder,
            task.state,
            task.labels,
            task.assignees,
            task.reportedBy,
            task.consistOf,
            task.createdAt,
            task.updatedAt
        )
    }
}