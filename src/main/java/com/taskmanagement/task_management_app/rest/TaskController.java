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
import java.util.Optional;

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

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        log.info("Getting task by id");
        Task task = taskService.getTaskById(id);
        return ResponseEntity.noContent().build().hasBody()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(task);
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Task> deleteTaskById(@PathVariable Long id) {
        log.warn("The following task will be deleted: {}", id);
        return ResponseEntity.status(
                taskService.deleteTaskById(id))
                .build();
    }
}
