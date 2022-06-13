package de.tom.demo.taskapp.entities.tasks

import de.tom.demo.taskapp.Constants
import org.springframework.data.rest.core.config.RepositoryRestConfiguration
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.LinkRelation
import org.springframework.hateoas.server.RepresentationModelProcessor
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.stereotype.Component

@Component
class TaskProcessor(val configuration: RepositoryRestConfiguration): RepresentationModelProcessor<EntityModel<Task>> {
    override fun process(model: EntityModel<Task>): EntityModel<Task> {
        val task = model.content
        if (task?.state == Constants.TASK_OPEN) {
            model.add(linkTo<TaskController> { closeTask(model.content?.id ?: "") }
                .withRel(LinkRelation.of("close") ))
        }
        if (task?.state == Constants.TASK_CLOSED) {
            model.add(linkTo<TaskController> { openTask(model.content?.id ?: "") }
                .withRel(LinkRelation.of("open") ))
        }
        return model
    }

}