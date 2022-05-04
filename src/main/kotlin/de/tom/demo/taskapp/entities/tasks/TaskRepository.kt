package de.tom.demo.taskapp.entities.tasks

import de.tom.demo.taskapp.entities.Task
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

/**
 *  JPA Repository: to interact with H2 database
 */
@Repository
interface TaskRepository : MongoRepository<Task, String> {
    @Query("{ \$or : [{'assignees' : { \$elemMatch : {'email': '?0' }}}, {'reportedBy.email' : '?0' }]}")
    fun findAllUserTasks(email: String): List<Task>

    @Query("{'reportedBy.email' : '?0' }")
    fun findAllTasksReportedByUser(email: String): List<Task>

    @Query("{'assignees' : { \$elemMatch : {'email': '?0' }}}")
    fun findAllTasksAssignedToUser(email: String): List<Task>

}
