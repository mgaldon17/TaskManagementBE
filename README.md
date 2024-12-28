# Task Management App Backend

This is a simple Task Management application built with Spring Boot and PostgreSQL.

## Features

- Create a new task
- Update an existing task
- Delete a task
- Get all tasks

## Technologies Used

- Java
- Spring Boot
- PostgreSQL
- Gradle

## Setup and Installation

1. Clone the repository to your local machine.
2. Ensure you have Java and Gradle installed on your machine.
3. Update the `application.properties` file with your PostgreSQL database details.
4. Run the application using the command `./gradlew bootRun`.

## API Endpoints

- `GET /tasks`: Fetch all tasks
- `POST /tasks`: Create a new task. The request body should be a JSON object representing the task. Example: `{"name": "Task Name", "done": false, "created": "2022-01-01 00:00:00", "priority": "HIGH"}`
- `PUT /tasks/{id}`: Update an existing task. The request body should be a JSON object representing the task. Example: `{"name": "New Task Name", "done": true, "created": "2022-01-01 00:00:00", "priority": "LOW"}`
- `DELETE /tasks/{id}`: Delete a task

## Database

The application uses a PostgreSQL database. The database configuration is located in the `application.properties` file.

## Logging

The application uses SLF4J with Logback for logging. The logging configuration is located in the `application.properties` file.

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.
