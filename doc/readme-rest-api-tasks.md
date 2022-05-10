# Definition of REST API tasks

Content:
* [GET /api/tasks](#GET/api/tasks)
* [GET /api/tasks/{id}](#GET/api/tasks/{id})
* [POST /api/tasks](#POST/api/tasks)
* [PUT /api/tasks/{id}](#POST/api/tasks/{id})
* [DELETE /api/tasks/{id}](#DELETE/api/tasks/{id})
* [GET /api/tasks/{id}/reportedby](#GET/api/tasks/{id}/reportedby)
* [GET /api/tasks/{id}/assignees](#GET/api/tasks/{id}/assignees)

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
                "createdAt": "2022-05-06T09:24:08.66346"
            },
            ...
        ]
    }

## <a name="GET/api/tasks/{id}"></a> GET /api/tasks/{id}
Get the tasks with the id of the logged-in user from the database.
The task must be reported by the logged-in user otherwise the server
returns an error response.

### Request:
Content type: application/json

|                | Type              | Description             |
|----------------|-------------------|-------------------------|
| Authorization  | Bearer Token      | access token from login |                      

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
        "createdAt": "2022-05-06T09:24:08.66346"
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
            "createdAt": "2022-05-06T09:20:43.174"
        },
        "consistOf": {
            "id": "6274b02a46439e735105650f", "name": "p1",
            "createdAt": "2022-05-06T09:20:39.844"
        },
        "createdAt": "2022-05-06T09:24:08.66346"
     }


## <a name="DELETE/api/tasks/{id}"></a> DELETE /api/tasks/{id}
Remove a tasks with id from the database.
The task must be reported by the logged-in user otherwise the server
returns an error response.

### Request:
Content type: application/json

|               | Type         | Description             |
|---------------|--------------|-------------------------|
| Authorization | Bearer Token | access token from login |                      

### Response:
Content type: application/json

| Code | Status       | Content / Message                                       |
|------|--------------|---------------------------------------------------------|
| 201  | Ok           |                                                         |
| 401  | Unauthorized | Full authentication is required to access this resource |
| 401  | Unauthorized | The Token has expired on ...                            |
| 404  | Not found    | Task with id ... not found                              |


## <a name="PUT/api/tasks"></a> PUT /api/tasks/{id}
Update the content of a task with the id.
The task must be reported by the logged-in user otherwise the server
returns an error response.

### Request:
Content type: application/json

|               | Type         | Description             |
|---------------|--------------|-------------------------|
| Authorization | Bearer Token | access token from login |                      
| Body          | JSON data    | task data               |

Example value of request body:

    {
        "text": "Updated Task",
        "day": "2022-03-03",
        "reminder": false,
        "projectName": "p1"
    }

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
        "text": "Updated Task",
        "description": "One time in week food must be bought.",
        "day": "2022-03-03", "reminder": false,
        "state": "Created", "labels": [],
        "assignees": [],
        "reportedBy": "/api/users/6274b02b46439e7351056510",
        "consistOf": "/api/projects/6274b02a46439e735105650f",
        "createdAt": "2022-05-06T09:24:08.66346", 
        "updatedAt": "2022-05-08T12:20:00.13456"
    }

## <a name="GET/api/tasks/{id}/reportedby"></a> GET /api/tasks/{id}/reportedby
Get the user who has reported the task with id.
The task must be reported by the logged-in user otherwise the server
returns an error response.

### Request:
Content type: application/json

|               | Type         | Description             |
|---------------|--------------|-------------------------|
| Authorization | Bearer Token | access token from login |                      

### Response:
Content type: application/json

| Code | Status       | Content / Message                                       |
|------|--------------|---------------------------------------------------------|
| 201  | Ok           | See example for response body below                     |
| 401  | Unauthorized | Full authentication is required to access this resource |
| 401  | Unauthorized | The Token has expired on ...                            |
| 404  | Not found    | Task with id ... not found                              |

Example value of response body in case of code 201:

    {
        {
            "id": "6274b02b46439e7351056510", "name": "John Doe", "password": "...",
            "email": "john.doe@test.com", "roles": ["ROLE_USER"],
            "createdAt": "2022-05-06T09:20:43.174"
        }
    }


## <a name="GET/api/tasks/{id}/assignees"></a> GET /api/tasks/{id}/assignees
Get the list of users that are assigned to the task with id.
The task must be reported by the logged-in user otherwise the server
returns an error response.

### Request:
Content type: application/json

|               | Type         | Description             |
|---------------|--------------|-------------------------|
| Authorization | Bearer Token | access token from login |                      

### Response:
Content type: application/json

| Code | Status       | Content / Message                                       |
|------|--------------|---------------------------------------------------------|
| 201  | Ok           | See example for response body below                     |
| 401  | Unauthorized | Full authentication is required to access this resource |
| 401  | Unauthorized | The Token has expired on ...                            |
| 404  | Not found    | Task with id ... not found                              |

Example value of response body in case of code 201:

    {
        [
            {
                "id": "6274b02b46439e7351056510", "name": "John Doe", "password": "...",
                "email": "john.doe@test.com", "roles": ["ROLE_USER"],
                "createdAt": "2022-05-06T09:20:43.174"
            },
            {
                "id": "6274b02b46439eee51056511", "name": "Jane Doe", "password": "...",
                "email": "jane.doe@test.com", "roles": ["ROLE_USER"],
                "createdAt": "2022-05-06T09:21:13.100"
            },
        ]
    }
