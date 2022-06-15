package de.tom.demo.taskapp.entities.tasks

import de.tom.demo.taskapp.Constants
import org.springframework.data.rest.core.config.RepositoryRestConfiguration
import org.springframework.hateoas.LinkRelation
import org.springframework.hateoas.server.RepresentationModelProcessor
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.stereotype.Component

@Component
class TaskProcessor(val configuration: RepositoryRestConfiguration): RepresentationModelProcessor<TaskModel> {
    override fun process(model: TaskModel): TaskModel {
        if (model.state == Constants.TASK_OPEN) {
            model.add(linkTo<TaskController> { closeTask(model.id) }
                .withRel(LinkRelation.of("close") ))
        }
        if (model.state == Constants.TASK_CLOSED) {
            model.add(linkTo<TaskController> { openTask(model.id) }
                .withRel(LinkRelation.of("open") ))
        }
        return model
    }

}