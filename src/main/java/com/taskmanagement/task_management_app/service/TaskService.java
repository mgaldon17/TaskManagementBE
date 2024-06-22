package com.taskmanagement.task_management_app.service;

import com.taskmanagement.task_management_app.model.Priority;
import com.taskmanagement.task_management_app.model.Task;
import com.taskmanagement.task_management_app.repository.TaskRepository;
import com.taskmanagement.task_management_app.rest.TaskController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public ResponseEntity<String> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
                .map(Task::toString)
                .reduce((t1, t2) -> t1 + "\n" + t2)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("No tasks found");
                    return ResponseEntity.noContent().build();
                });
    }

    public ResponseEntity<String> createTask(Task task) {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
                .filter(t -> t.getName().equals(task.getName()))
                .findAny()
                .map(t -> ResponseEntity.badRequest().body("Task already exists"))
                .orElseGet(() -> {
                    taskRepository.save(task);
                    log.warn("Task created successfully: task={}", task);
                    return ResponseEntity.ok("Task created successfully");
                });
    }

    public ResponseEntity<String> updateTask(Long id, Task updatedTask) {
        return taskRepository.findById(id)
                .map(task -> {
                    BeanUtils.copyProperties(updatedTask, task, "id");
                    taskRepository.save(task);
                    log.info("Task updated successfully: id={}, updatedTask={}", id, updatedTask);
                    return ResponseEntity.ok("Task updated successfully");
                })
                .orElseGet(() -> ResponseEntity.badRequest().body("Task not found"));
    }

    public ResponseEntity<String> deleteTask(Long id) {
        return taskRepository.findById(id)
                .map(task -> {
                    taskRepository.delete(task);
                    log.warn("Task deleted successfully: id={}", id);
                    return ResponseEntity.ok("Task deleted successfully");
                })
                .orElseGet(() -> ResponseEntity.badRequest().body("Task not found"));
    }

    public ResponseEntity<String> updateTaskName(Long id, String newName) {
        return taskRepository.findById(id)
                .map(task -> {
                    task.setName(newName);
                    taskRepository.save(task);
                    log.warn("Task updated successfully: id={}, newName={}", id, newName);
                    return ResponseEntity.ok("Task updated successfully");
                })
                .orElseGet(() -> ResponseEntity.badRequest().body("Task not found"));
    }

    public ResponseEntity<String> updateTaskPriority(Long id, String newPriority) {
        return taskRepository.findById(id)
                .map(task -> {
                    task.setPriority(Priority.valueOf(newPriority));
                    taskRepository.save(task);
                    log.warn("Task updated successfully: id={}, newPriority={}", id, newPriority);
                    return ResponseEntity.ok("Task updated successfully");
                })
                .orElseGet(() -> ResponseEntity.badRequest().body("Task not found"));
    }
}
