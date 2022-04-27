# Task Tracker REST API
This project implements a REST API server that manages tasks for a task's tracker system.
The tasks are stored in an H2 in-memory database. 

## Usage
Build the application with gradle:

    ./gradlew clean build

Run the application:

    ./gradlew bootrun

The REST API server runs at the port 5000.

## Definitions

### JSON data definition
Example JSON data:

    {
        "id": "cf2ae94e-3312-418c-8a57-1bfa9a36c63b",
        "text": "Food shopping",
        "day": "2022-02-24 18:00:00.000.00.0",
        "reminder":true
    }

### REST API
#### Endpoints for user management
| Method | URL        | Action                                            |
|--------|------------|---------------------------------------------------|
| POST   | /login     | sign in with email and password                   |
| POST   | /register  | register new user with name, email and password   |
| GET    | /api/users | get the list of registered users (only for admin) |

After /register the user must sign in via a /login request. 
The response of the /login request contains the user details and two tokens: an access token for 
authorization the access to the resources, and a refresh token to renew the access token if it is expired. 
The token must be sent in the authorization header as Bearer Token.

#### Endpoints for tasks management
In order to use the tasks endpoints, the user must be authenticated before.

| Method | URL             | Action                       |
|--------|-----------------|------------------------------|
| GET    | /api/tasks      | get all tasks from database  |
| GET    | /api/tasks/{id} | get task details based on id |
| POST   | /api/tasks      | create a new task            |
| DELETE | /api/tasks/{id} | remove/delete task by id     |
| PUT    | /api/tasks/{id} | update task details by id    |



