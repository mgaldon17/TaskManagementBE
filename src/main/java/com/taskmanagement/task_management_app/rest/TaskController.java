package com.taskmanagement.task_management_app.rest;

import com.taskmanagement.task_management_app.model.Task;
import com.taskmanagement.task_management_app.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TaskService taskService;

    @GetMapping
    public ResponseEntity<String> getAllTasks() {
        log.info("Getting all tasks");
        return taskService.getAllTasks();
    }

    @PostMapping
    public ResponseEntity<String> createTask(@RequestBody Task task) {
        log.info("Creating task: " + task);
        return taskService.createTask(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateTask(@PathVariable Long id, @RequestBody Task updatedTask) {
        log.info("Updating task with: " + id + " to: " + updatedTask);
        return taskService.updateTask(id, updatedTask);
    }

    @PutMapping("/updateTaskName")
    public ResponseEntity<String> updateTaskName(@RequestParam Long id, @RequestParam String newName) {
        log.info("Updating task name of task with: " + id + " to: " + newName);

        return (newName == null || newName.isEmpty())
                ? logAndReturnError("New name cannot be empty")
                : (id == null)
                ? logAndReturnError("Task ID cannot be empty")
                : taskService.updateTaskName(id, newName);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable Long id) {
        log.warn("The following task will be deleted: " + id);
        return taskService.deleteTask(id);
    }

    @PutMapping("/updateTaskPriority")
    public ResponseEntity<String> updateTaskPriority(@RequestParam Long id, @RequestParam String newPriority) {
        log.info("Updating task priority of task with: " + id + " to: " + newPriority);

        return (newPriority == null || newPriority.isEmpty())
                ? logAndReturnError("New priority cannot be empty")
                : (id == null)
                ? logAndReturnError("Task ID cannot be empty")
                : taskService.updateTaskPriority(id, newPriority);
    }

    private ResponseEntity<String> logAndReturnError(String errorMessage) {
        log.error(errorMessage);
        return ResponseEntity.badRequest().body(errorMessage);
    }
}
