package de.tom.demo.taskapp.entities.tasks

import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.stereotype.Component

@Component
class TaskModelAssembler : RepresentationModelAssembler<Task, EntityModel<Task>> {

    override fun toModel(task: Task): EntityModel<Task> {
        return return EntityModel.of(task,
            linkTo<TaskController> { getById(task.id ?: "") }.withSelfRel(),
            linkTo<TaskController> { get(null) }.withRel("tasks"))
    }

}