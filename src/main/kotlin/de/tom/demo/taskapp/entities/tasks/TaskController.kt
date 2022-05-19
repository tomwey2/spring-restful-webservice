package de.tom.demo.taskapp.entities.tasks

import de.tom.demo.taskapp.Constants
import de.tom.demo.taskapp.TaskNotValidException
import de.tom.demo.taskapp.entities.Task
import de.tom.demo.taskapp.entities.TaskForm
import de.tom.demo.taskapp.entities.User
import de.tom.demo.taskapp.entities.projects.ProjectService
import de.tom.demo.taskapp.entities.users.UserService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

/**
 * REST Controller for the tasks resource with GET, POST, PUT and DELETE methods.
 */
//  @CrossOrigin(origins = ["http://localhost:3000"])
@RestController
@RequestMapping(Constants.PATH_TASKS)
class TaskController(val service: TaskService, val userService: UserService, val projectService: ProjectService) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    /**
     * GET /api/tasks
     *
     * Get all tasks of the logged-in user from the database.
     * The task must be reported by the logged-in user otherwise the server
     * returns an error response.
     *
     * @return list of tasks
     *
     * @sample
     * Request:
     * GET /api/tasks
     * Content type: application/json
     * Authorization: Bearer Token: access token from login
     *
     * Response:
     * Content type: application/json
     * Code 201 - Ok: See example for response body below
     *
     * Error cases:
     * Code 401 - Unauthorized: Full authentication is required to access this resource
     * Code 401 - Unauthorized: The Token has expired on ...
     *
     * Example value of response content:
     * {
     *      [
     *          {
     *              "id": "6274b0f846439e7351056517",
     *              "text": "Food shopping",
     *              "description": "One time in week food must be bought.",
     *              "day": "2022-03-01", "reminder": true,
     *              "state": "Created", "labels": [],
     *              "assignees": [],
     *              "reportedBy": "/api/users/6274b02b46439e7351056510",
     *              "consistOf": "/api/projects/6274b02a46439e735105650f",
     *              "createdAt": "2022-05-06T09:24:08.66346", "updatedAt": null
     *          },
     *          ...
     *      ]
     *  }
     */
    @GetMapping(path = [""])
    @ResponseStatus(HttpStatus.OK)
    fun getAll(): List<Task> = service.getAllTasksReportedByUser(userService.getLoggedInUser())

    /**
     * GET /api/tasks/{id}
     *
     * Get the tasks with the id of the logged-in user from the database.
     * The task must be reported by the logged-in user otherwise the server
     * returns an error response.
     *
     * @param id id of the requested task
     * @return the task with id if exists otherwise error message
     *
     * @sample
     * Request:
     * GET /api/tasks/6274b0f846439e7351056517
     * Authorization: Bearer Token: access token from login
     * Content type: application/json
     *
     * Response:
     * Content type: application/json
     * Code 201 - Ok: See example for response body below
     *
     * Error cases:
     * Code 401 - Unauthorized: Full authentication is required to access this resource
     * Code 401 - Unauthorized: The Token has expired on ...
     * Code 404 - Not found: Task with id ... not found
     *
     * Example value of response content:
     *  {
     *      "id": "6274b0f846439e7351056517",
     *      "text": "Food shopping",
     *      "description": "One time in week food must be bought.",
     *      "day": "2022-03-01", "reminder": true,
     *      "state": "Created", "labels": [],
     *      "assignees": [],
     *      "reportedBy": "/api/users/6274b02b46439e7351056510",
     *      "consistOf": "/api/projects/6274b02a46439e735105650f",
     *      "createdAt": "2022-05-06T09:24:08.66346", "updatedAt": null
     *  }
     */
    @GetMapping(path = ["/{id}"])
    @ResponseStatus(HttpStatus.OK)
    fun getById(@PathVariable id: String): Task = service.getTaskOfUser(id, userService.getLoggedInUser())

    /**
     * POST /api/tasks
     *
     * Add a new task to the database and assign it to the project of the given project id.
     * The task is reported by the logged-in user.<p>
     * @param body task data
     * @return created task
     *
     * @sample
     * Request:
     * POST /api/tasks/
     * Content type: application/json
     * Authorization: Bearer Token: access token from login
     * Body: JSON data: task data
     *
     * Example value of request body:
     *  {
     *      "text": "New Task",
     *      "day": "2022-03-01",
     *      "reminder": true,
     *      "projectName": "p1"
     *  }
     *
     * Response:
     * Content type: application/json
     * Code 201 - Ok: See example for response body below
     *
     * Error cases:
     * Code 401 - Unauthorized: Full authentication is required to access this resource
     * Code 401 - Unauthorized: The Token has expired on ...
     * Code 404 - Not found: Project with id ... not found
     * Code 400 - Bad Request: JSON parse error ...
     *
     * Example value of response content:
     *  {
     *      "id": "6274b0f846439e7351056517",
     *      "text": "New Task", "description": null, "day": "2022-03-01", "reminder": true,
     *      "state": "Created", "labels": [], "assignees": [],
     *      reportedBy": {
     *          "id": "6274b02b46439e7351056510", "name": "John Doe", "password": "...",
     *          "email": "john.doe@test.com", "roles": ["ROLE_USER"],
     *          "createdAt": "2022-05-06T09:20:43.174","updatedAt": null
     *      },
     *      "consistOf": {
     *          "id": "6274b02a46439e735105650f", "name": "p1",
     *          "createdAt": "2022-05-06T09:20:39.844","updatedAt": null
     *      },
     *      "createdAt": "2022-05-06T09:24:08.66346", "updatedAt": null
     *  }
     */
    @PostMapping(path = [""])
    @ResponseStatus(HttpStatus.CREATED)
    fun post(@RequestBody body: TaskForm): Task =
        if (body.text.isEmpty())
            throw TaskNotValidException("add task fields: text, day, reminder")
        else {
            val reportedBy = userService.getLoggedInUser()
            service.addTask(body.text, body.description, TaskUtils.convertStringToLocalDate(body.day), body.reminder,
                reportedBy, null)
        }

    /**
     * DELETE /api/tasks/{id}
     *
     * Remove a tasks with id from the database.
     * The task must be reported by the logged-in user otherwise the server
     * returns an error response.
     *
     * @param id id of the requested task
     * @return code 200 if task is deleted otherwise an error message
     *
     * @sample
     * Request:
     * DELETE /api/tasks/6274b0f846439e7351056517
     * Authorization: Bearer Token: access token from login
     * Content type: application/json
     *
     * Response:
     * Code 201 - Ok
     *
     * Error cases:
     * Code 401 - Unauthorized: Full authentication is required to access this resource
     * Code 401 - Unauthorized: The Token has expired on ...
     * Code 404 - Not found: Task with id ... not found
     */
    @DeleteMapping(path = ["/{id}"])
    @ResponseStatus(HttpStatus.OK)
    fun delete(@PathVariable id: String): Unit = service.deleteTaskOfUser(id, userService.getLoggedInUser())

    /**
     * PUT /api/tasks/6274b0f846439e7351056517
     *
     * Update the content of a task with the id.
     * The task must be reported by the logged-in user otherwise the server
     * returns an error response.
     *
     * @param body task data
     * @return updated task
     *
     * @sample
     * Request:
     * PUT /api/tasks/6274b0f846439e7351056517
     * Content type: application/json
     * Authorization: Bearer Token: access token from login
     * Body: JSON data: task data
     *
     * Example value of request body:
     *  {
     *      "text": "Updated Task",
     *      "day": "2022-03-03",
     *      "reminder": false,
     *      "projectName": "p1"
     *  }
     *
     * Response:
     * Content type: application/json
     * Code 200 - Ok: See example for response body below
     *
     * Error cases:
     * Code 401 - Unauthorized: Full authentication is required to access this resource
     * Code 401 - Unauthorized: The Token has expired on ...
     * Code 404 - Not found: Task with id ... not found
     *
     * Example value of response content:
     *  {
     *      "id": "6274b0f846439e7351056517",
     *      "text": "Updated Task",
     *      "description": "One time in week food must be bought.",
     *      "day": "2022-03-03", "reminder": false,
     *      "state": "Created", "labels": [],
     *      "assignees": [],
     *      "reportedBy": "/api/users/6274b02b46439e7351056510",
     *      "consistOf": "/api/projects/6274b02a46439e735105650f",
     *      "createdAt": "2022-05-06T09:24:08.66346", "updatedAt": null
     *  }
     */
    @PutMapping(path = ["/{id}"])
    @ResponseStatus(HttpStatus.OK)
    fun put(@PathVariable id: String, @RequestBody body: TaskForm): Task =
        service.updateTask(id, body.text, TaskUtils.convertStringToLocalDate(body.day), body.reminder,
            userService.getLoggedInUser())

    /**
     * GET /api/tasks/{id}/reportedby
     *
     * Get the user who has reported the task with id.
     * The task must be reported by the logged-in user otherwise the server
     * returns an error response.
     *
     * @param id id of the requested task
     * @return the user that has reported the task with id.  if not exists then returns error message.
     *
     * @sample
     * Request:
     * GET /api/tasks/6274b0f846439e7351056517/reportedby
     * Authorization: Bearer Token: access token from login
     * Content type: application/json
     *
     * Response:
     * Content type: application/json
     * Code 201 - Ok: See GET /api/tasks/{id}
     *
     * Error cases:
     * Code 401 - Unauthorized: Full authentication is required to access this resource
     * Code 401 - Unauthorized: The Token has expired on ...
     * Code 404 - Not found: Task with id ... not found
     */
    @GetMapping(path = ["/{id}/reportedby"])
    @ResponseStatus(HttpStatus.OK)
    fun getReporterOfTaskWithId(@PathVariable id: String): User =
        service.getTaskOfUser(id, userService.getLoggedInUser())
            .reportedBy

    /**
     * GET /api/tasks/{id}/assignees
     *
     * Get the list of users that are assigned to the task with id.
     * The task must be reported by the logged-in user otherwise the server
     * returns an error response.
     *
     * @param id id of the requested task
     * @return the list of user that are assigned to the task with id.
     *          if not exists then returns error message.
     *
     * @sample
     * Request:
     * GET /api/tasks/6274b0f846439e7351056517/assignees
     * Authorization: Bearer Token: access token from login
     * Content type: application/json
     *
     * Response:
     * Content type: application/json
     * Code 201 - Ok:
     *
     * Error cases:
     * Code 401 - Unauthorized: Full authentication is required to access this resource
     * Code 401 - Unauthorized: The Token has expired on ...
     * Code 404 - Not found: Task with id ... not found
     *
     * {
     *      [
     *          {
     *              "id": "6274b02b46439e7351056510", "name": "John Doe", "password": "...",
     *              "email": "john.doe@test.com", "roles": ["ROLE_USER"],
     *              "createdAt": "2022-05-06T09:20:43.174","updatedAt": null
     *          }
     *      ]
     *  },

     */
    @GetMapping(path = ["/{id}/assignees"])
    @ResponseStatus(HttpStatus.OK)
    fun getAssigneesOfTaskWithId(@PathVariable id: String): List<User> =
        service.getTaskOfUser(id, userService.getLoggedInUser())
            .assignees


    /**
     * Change the list of users that are assigned to the task with id
     * (i.e. replace the old list of users with the new one).
     * Only the logged-in user that has reported the task can change
     * the list of assignees otherwise the server returns an error response.
     */
    @PutMapping(path = ["/{id}/assignees"])
    @ResponseStatus(HttpStatus.OK)
    fun changeAssignees(@PathVariable id: String, @RequestBody assignees: List<User>): Task {
        val task = service.getTaskOfUser(id, userService.getLoggedInUser())
        return service.changeAssignedUsers(task, assignees)
    }


}
