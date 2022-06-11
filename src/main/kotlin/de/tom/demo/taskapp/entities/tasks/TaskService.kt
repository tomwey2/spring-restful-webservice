package de.tom.demo.taskapp.entities.tasks

import de.tom.demo.taskapp.Constants
import de.tom.demo.taskapp.TaskNotFoundException
import de.tom.demo.taskapp.entities.Task
import de.tom.demo.taskapp.entities.User
import de.tom.demo.taskapp.entities.users.UserService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate

/**
 * Tasks service class with business functions to add, update and delete a tasks.
 */
@Service
class TaskService(val db: TaskRepository, val userService: UserService) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    /**
     * Gets all tasks which are either reported by or assigned to the user.
     * If the user has the ADMIN role then gets all tasks.
     */
    fun getTasks(user: User): List<Task> {
        //val user = userService.getLoggedInUser()
        return if (user.roles.contains(Constants.ROLE_ADMIN)) db.findAll() else db.findAllUserTasks(user.email)
    }

    /**
     * Gets all tasks that are reported by the user.
     * The admin gets all tasks.
     */
    fun getAllTasksReportedByUser(user: User): List<Task> {
        return if (user.roles.contains(Constants.ROLE_ADMIN)) db.findAll() else db.findAllTasksReportedByUser(user.email)
    }

    /**
     * Extract the value from a query string. if the query string contains a substring like
     * 'key:value' then the function returns the value.
     * If the key ist not found in the query string then the function returns an empty string.
     */
    fun getValueFromQueryByKey(query: String, key: String): String {
        val value: String = Regex(pattern = """(?i)$key(?-i):(?:(@me)|([\w\d._%+-@][^, ]+))""")
            .find(input = query)?.value.orEmpty()
        return value.replaceFirst("$key:", "", ignoreCase = true);
    }

    /**
     * Gets all tasks that are fulfilled by the query condition.
     * The query can contain the following key:value pairs: is:<open|closed>. reportedby:<name>,
     * assignedto:<name>. The key:value pairs must be seperated by semicolon.
     * If <name> has the value '@me' then the value is replaced by the name of the given username.
     */
    fun getTasksByQuery(query: String, user: User): List<Task> {
        val searchIsOpen = query.contains("is:open", ignoreCase = true)
        val searchIsClosed = query.contains("is:closed", ignoreCase = true)
        val searchState = if (searchIsOpen) Constants.TASK_OPEN else if (searchIsClosed) Constants.TASK_CLOSED  else ""

        val searchReporter = query.contains("reportedby:", ignoreCase = true)
        var searchReporterName = if (searchReporter) getValueFromQueryByKey(query, "reportedby") else ""
        if (searchReporterName == "@me") {
            searchReporterName = user.username
        }

        val searchAssignee = query.contains("assignedto:", ignoreCase = true)
        var searchAssigneeName = if (searchAssignee) getValueFromQueryByKey(query, "assignedto") else ""
        if (searchAssigneeName == "@me") {
            searchAssigneeName = user.username
        }

        log.info("!!!! $searchState $searchReporter && $searchAssignee")
        return if ((searchIsOpen || searchIsClosed) && searchReporter && searchAssignee) {
            log.info("findTasksSearchStateReporterAssignee($searchState, $searchReporterName, $searchAssigneeName)")
            db.findTasksByStateAndReporterAndAssignee(searchState, searchReporterName, searchAssigneeName)
        } else if ((searchIsOpen || searchIsClosed) && searchReporter && !searchAssignee) {
            log.info("findTasksSearchStateReporter($searchState, $searchReporterName)")
            db.findTasksByStateAndReporter(searchState, searchReporterName)
        } else if ((searchIsOpen || searchIsClosed) && !searchReporter && searchAssignee) {
            log.info("findTasksSearchStateAssignee($searchState, $searchAssigneeName)")
            db.findTasksByStateAndAssignee(searchState, searchAssigneeName)
        } else if ((searchIsOpen || searchIsClosed) && !searchReporter && !searchAssignee) {
            log.info("findTasksSearchState($searchState)")
            db.findTasksByState(searchState)
        } else if (!(searchIsOpen || searchIsClosed) && searchReporter && searchAssignee) {
            log.info("findTasksSearchReporterAssignee($searchReporterName, $searchAssigneeName)")
            db.findTasksByReporterAndAssignee(searchReporterName, searchAssigneeName)
        } else if (!(searchIsOpen || searchIsClosed) && searchReporter && !searchAssignee) {
            log.info("findTasksSearchReporter($searchReporterName)")
            db.findTasksByReporter(searchReporterName)
        } else if (!(searchIsOpen || searchIsClosed) && !searchReporter && searchAssignee) {
            log.info("findTasksSearchAssignee($searchAssigneeName)")
            db.findTasksByAssignee(searchAssigneeName)
        } else listOf()
    }

    /**
     * Gets all tasks that are assigned to the user.
     * The admin gets all tasks.
     */
    fun getAllTasksAssignedToUser(user: User): List<Task> {
        //val user = userService.getLoggedInUser()
        return if (user.roles.contains(Constants.ROLE_ADMIN)) db.findAll() else db.findAllTasksAssignedToUser(user.email)
    }

    /**
     * Get the task of the user with a given id.
     */
    fun getTaskOfUser(id: String, user: User): Task {
        val tasks = getAllTasksReportedByUser(user)
        return tasks.firstOrNull { task: Task -> task.id.equals(id) } ?: throw TaskNotFoundException(id)
    }

    /**
     * Add a new task and assign it to a project. The task must have a user that reported the task.
     * It can have optionally a user who is assigned to it.
     */
    fun addTask(text: String, description: String?, day: LocalDate, reminder: Boolean,
                reportedBy: User, assignedTo: User?): Task {
        val assignees = if (assignedTo != null) listOf(assignedTo) else listOf()
        val newTask = Task(null, text, description, day, reminder,
            Constants.TASK_OPEN, listOf(), assignees, reportedBy)
        return db.save(newTask)
    }

    /**
     * Assign a user to a task. This update the list of assignees of a task.
     */
    fun assignedUserToTask(task: Task, user: User): Task =
        db.save(task.copy(assignees = task.assignees.plus(user)))

    /**
     * Change the list of users that are assigned to the task with id
     * (i.e. replace the old list of users with the new one).
     */
    fun changeAssignedUsers(task: Task, assignees: List<User>): Task =
        db.save(task.copy(assignees = assignees))

    /**
     * Change the list of labels of the task with id
     */
    fun changeLabels(task: Task, labels: List<String>): Task =
        db.save(task.copy(labels = labels))

    /**
     * Delete a user task that have the given id.
     */
    fun deleteTaskOfUser(id: String, user: User): Unit {
        val task: Task = getTaskOfUser(id, user)
        db.delete(task)
    }

    /**
     * Delete all tasks.
     * TODO: delete only own tasks
     */
    fun deleteAll(): Unit {
        db.deleteAll()
    }

    /**
     * Update the content of a task. That can be the text, day or the reminder flag.
     * TODO: add the description field
     */
    fun updateTask(id: String, text: String, description: String?, day: LocalDate, reminder: Boolean, state: String?,
                   user: User): Task {
        val updatedTask = getTaskOfUser(id, user).copy(text = text, description = description, day = day,
            reminder = reminder, state = state ?: Constants.TASK_OPEN)
        return db.save(updatedTask)
    }

    fun changeState(id: String, state: String, user: User): Task {
        val updatedTask = getTaskOfUser(id, user).copy(state = state)
        return db.save(updatedTask)
    }
}
