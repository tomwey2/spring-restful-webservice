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

| Method | URL         | Action                       |
|--------|-------------|------------------------------|
| GET    | /tasks      | get all tasks from database  |
| GET    | /tasks/{id} | get task details based on id |
| POST   | /tasks      | create a new task            |
| DELETE | /tasks/{id} | remove/delete task by id     |
| PUT    | /tasks/{id} | update task details by id    |



