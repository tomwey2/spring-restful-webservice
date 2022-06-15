package de.tom.demo.taskapp.entities.tasks

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

/**
 *  JPA Repository: to interact with H2 database
 */
@Repository
interface TaskRepository : MongoRepository<Task, String> {
    //@Query("{ \$or : [{'reportedBy.username' : { \$regex: '(?i)?0(?-i)'}, 'assignees.username' : { \$regex: '(?i)?1(?-i)'} }]}")
    @Query("{ \$or : [{'assignees' : { \$elemMatch : {'username': '?0' }}}, {'reportedBy.username' : '?0' }]}")
    fun findAllUserTasks(username: String): List<Task>

    @Query("{'reportedBy.username' : { \$regex: '(?i)?1(?-i)'}}")
    fun findAllTasksReportedByUser(username: String): List<Task>

    @Query("{'state': '?1', 'reportedBy.email' : '?0' }")
    fun findTasksReportedByUser(email: String, state: String): List<Task>

    @Query("{'assignees' : { \$elemMatch : {'email': '?0' }}}")
    fun findAllTasksAssignedToUser(email: String): List<Task>

    @Query("{'state': '?0', 'reportedBy.username' : { \$regex: '(?i)?1(?-i)'}, 'assignees.username' : { \$regex: '(?i)?2(?-i)'} }")
    fun findTasksByStateAndReporterAndAssignee(state: String, reportedBy: String, assignedTo: String): List<Task>

    @Query("{'state': '?0', 'reportedBy.username' : { \$regex: '(?i)?1(?-i)'}}")
    fun findTasksByStateAndReporter(state: String, reportedBy: String): List<Task>

    @Query("{'state': '?0', 'assignees.username' : { \$regex: '(?i)?1(?-i)'} }")
    fun findTasksByStateAndAssignee(state: String, assignedTo: String): List<Task>

    @Query("{'reportedBy.username' : { \$regex: '(?i)?0(?-i)'}, 'assignees.username' : { \$regex: '(?i)?1(?-i)'} }")
    fun findTasksByReporterAndAssignee(reportedBy: String, assignedTo: String): List<Task>

    @Query("{'state': '?0' }")
    fun findTasksByState(state: String): List<Task>

    @Query("{'reportedBy.username' : { \$regex: '(?i)?0(?-i)'} }")
    fun findTasksByReporter(reportedBy: String): List<Task>

    @Query("{'assignees.username' : '?0' }")
    fun findTasksByAssignee(assignedTo: String): List<Task>
}
