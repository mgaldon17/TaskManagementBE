package com.taskmanagement.task_management_app.rest;

import com.taskmanagement.task_management_app.model.Task;
import com.taskmanagement.task_management_app.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TaskService taskService;

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        log.info("Getting all tasks");
        List<Task> tasks = taskService.getAllTasks();
        return ResponseEntity.noContent().build().hasBody()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(tasks);
    }

    @PostMapping
    public ResponseEntity<Task> createNewTask(@RequestParam String name,
                                                @RequestParam boolean done,
                                                @RequestParam String created,
                                                @RequestParam String priority
                                                ) {
        log.info("Creating task: {}, with priority: {}, done: {}, created: {}", name, priority, done, created);
        return ResponseEntity.status(
                taskService.createTask(name, done, created, priority))
                .build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTaskById(@PathVariable Long id,
                                                 @RequestParam String name,
                                                 @RequestParam boolean done,
                                                 @RequestParam String created,
                                                 @RequestParam String priority
                                                ) {
        log.info("Updating task with id={}, to task with name={}, done={}, created={} and priority={}", id, name, done, created, priority);
        return ResponseEntity.status(
                taskService.updateTaskById(id, name, done, created, priority))
                .build();
    }

    @PutMapping("/updateTaskName")
    public ResponseEntity<Task> updateTaskNameById(@RequestParam Long id, @RequestParam String newName) {
        log.info("Updating task name of task with id={} to: {}", id, newName);

        return (newName == null || newName.isEmpty())
                ? logAndReturnError("New name cannot be empty")
                : (id == null)
                ? logAndReturnError("Task ID cannot be empty")
                : ResponseEntity.status(
                        taskService.updateTaskName(id, newName))
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Task> deleteTaskById(@PathVariable Long id) {
        log.warn("The following task will be deleted: {}", id);
        return ResponseEntity.status(
                taskService.deleteTaskById(id))
                .build();
    }

    @PutMapping("/updateTaskPriority")
    public ResponseEntity<Task> updateTaskPriority(@RequestParam Long id, @RequestParam String newPriority) {
        log.info("Updating task priority of task with id={} to: {}", id, newPriority);

        return (newPriority == null || newPriority.isEmpty())
                ? logAndReturnError("New priority cannot be empty")
                : (id == null)
                ? logAndReturnError("Task ID cannot be empty")
                : ResponseEntity.status(
                        taskService.updateTaskPriority(id, newPriority))
                .build();
    }

    private ResponseEntity<Task> logAndReturnError(String errorMessage) {
        log.error(errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
