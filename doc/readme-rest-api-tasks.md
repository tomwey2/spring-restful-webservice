# Definition of REST API tasks

* [GET /api/tasks](#GET/api/tasks)
* [GET /api/tasks/{id}](#GET/api/tasks/{id})
* [POST /api/tasks](#POST/api/tasks)

## <a name="GET/api/tasks"></a> GET /api/tasks
Get all tasks of the logged-in user from the database.
The task must be reported by the logged-in user otherwise the server
returns an error response.

### Request:
Content type: application/json

|               | Type             | Description             |
|---------------|------------------|-------------------------|
| Authorization | Bearer Token     | access token from login |                      

### Response:
Content type: application/json

| Code | Status       | Content / Message                                       |
|------|--------------|---------------------------------------------------------|
| 201  | Ok           | See example for response body below                     |
| 401  | Unauthorized | Full authentication is required to access this resource |
| 401  | Unauthorized | The Token has expired on ...                            |

Example value of response body in case of code 201:

    {
        [
            {
                "id": "6274b0f846439e7351056517",
                "text": "Food shopping",
                "description": "One time in week food must be bought.",
                "day": "2022-03-01", "reminder": true,
                "state": "Created", "labels": [],
                "assignees": [],
                "reportedBy": "/api/users/6274b02b46439e7351056510",
                "consistOf": "/api/projects/6274b02a46439e735105650f",
                "createdAt": "2022-05-06T09:24:08.66346", "updatedAt": null
            },
            ...
        ]
    }

## <a name="GET/api/tasks/{id}"></a> GET /api/tasks/{id}
Get the tasks with the id of the logged-in user from the database.
The task must be reported by the logged-in user otherwise the server
returns an error response.

### Request:
GET /api/tasks/6274b0f846439e7351056517

|                | Type              | Description             |
|----------------|-------------------|-------------------------|
| Authorization  | Bearer Token      | access token from login |                      
| Content type   | application/json  |                         |                       

### Response:
Content type: application/json


| Code | Status       | Content / Message                                       |
|------|--------------|---------------------------------------------------------|
| 201  | Ok           | See example for response body below                     |
| 401  | Unauthorized | Full authentication is required to access this resource |
| 401  | Unauthorized | The Token has expired on ...                            |
| 404  | Not found    | Task with id ... not found                              |
 
Example value of response content:

    {
        "id": "6274b0f846439e7351056517",
        "text": "Food shopping",
        "description": "One time in week food must be bought.",
        "day": "2022-03-01", "reminder": true,
        "state": "Created", "labels": [],
        "assignees": [],
        "reportedBy": "/api/users/6274b02b46439e7351056510",
        "consistOf": "/api/projects/6274b02a46439e735105650f",
        "createdAt": "2022-05-06T09:24:08.66346", "updatedAt": null
    }

## <a name="POST/api/tasks"></a> POST /api/tasks
Add a new task to the database and assign it to the project of the given project id.
the task is reported by the logged-in user.

### Request
Content type: application/json

|               | Type         | Description             |
|---------------|--------------|-------------------------|
| Authorization | Bearer Token | access token from login |                      
| Body          | JSON data    | task data               |

Example value of request body:

    {
      "text": "New Task",
      "day": "2022-03-01",
      "reminder": true,
      "projectName": "p1"
    }

### Response
Content type: application/json

| Code | Status       | Content / Message                                       |
|------|--------------|---------------------------------------------------------|
| 201  | Ok           | See example for response body below                     |
| 400  | Bad Request  | JSON parse error ...                                    |
| 401  | Unauthorized | Full authentication is required to access this resource |
| 401  | Unauthorized | The Token has expired on ...                            |
| 404  | Not found    | Project not found: ...                                  |

Example value of response body in case of code 201:

    {
        "id": "6274b0f846439e7351056517",
        "text": "New Task", "description": null, "day": "2022-03-01", "reminder": true,
        "state": "Created", "labels": [], "assignees": [],
        reportedBy": {
            "id": "6274b02b46439e7351056510", "name": "John Doe", "password": "...",
            "email": "john.doe@test.com", "roles": ["ROLE_USER"],
            "createdAt": "2022-05-06T09:20:43.174","updatedAt": null
        },
        "consistOf": {
            "id": "6274b02a46439e735105650f", "name": "p1",
            "createdAt": "2022-05-06T09:20:39.844","updatedAt": null
        },
        "createdAt": "2022-05-06T09:24:08.66346", "updatedAt": null
     }


