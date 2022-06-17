package de.tom.demo.taskapp.entities.tasks

import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.stereotype.Component

data class TaskListModel(
    val open: Int,
    val closed: Int,
    val taskList: CollectionModel<TaskModel>
) : RepresentationModel<TaskListModel>()

@Component
class TaskListModelAssembler : RepresentationModelAssemblerSupport<TaskList, TaskListModel>
    (TaskController::class.java, TaskListModel::class.java) {

    override fun instantiateModel(taskList: TaskList): TaskListModel {
        val collectionModel = CollectionModel.of(taskList.entities,
            linkTo<TaskController> { get(taskList.query) }.withSelfRel())

        return TaskListModel(taskList.open, taskList.closed, collectionModel)
    }

    override fun toModel(taskList: TaskList): TaskListModel {
        return instantiateModel(taskList)
    }
}