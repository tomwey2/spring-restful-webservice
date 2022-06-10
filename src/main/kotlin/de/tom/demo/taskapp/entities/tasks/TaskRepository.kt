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

    @Query("{'state': '?1', 'reportedBy.email' : '?0' }")
    fun findTasksReportedByUser(email: String, state: String): List<Task>

    @Query("{'assignees' : { \$elemMatch : {'email': '?0' }}}")
    fun findAllTasksAssignedToUser(email: String): List<Task>

    @Query("{'state': '?0', 'reportedBy.email' : '?1', 'assignees.email' : '?2' }")
    fun findTasksByStateAndReporterAndAssignee(state: String, emailReportedBy: String, emailAssignedTo: String): List<Task>

    @Query("{'state': '?0', 'reportedBy.email' : '?1'}")
    fun findTasksByStateAndReporter(state: String, emailReportedBy: String): List<Task>

    @Query("{'state': '?0', 'assignees.email' : '?1' }")
    fun findTasksByStateAndAssignee(state: String, emailAssignedTo: String): List<Task>

    @Query("{'reportedBy.email' : '?0', 'assignees.email' : '?1' }")
    fun findTasksByReporterAndAssignee(emailReportedBy: String, emailAssignedTo: String): List<Task>

    @Query("{'state': '?0' }")
    fun findTasksByState(state: String): List<Task>

    @Query("{'reportedBy.email' : '?0'}")
    fun findTasksByReporter(emailReportedBy: String): List<Task>

    @Query("{'assignees.email' : '?0' }")
    fun findTasksByAssignee(emailAssignedTo: String): List<Task>
}
