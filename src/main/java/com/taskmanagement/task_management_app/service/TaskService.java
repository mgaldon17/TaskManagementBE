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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Optional;


@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public ResponseEntity<String> createTask(String name, Boolean done, String created, String priority) {
        Task newTask = Task.builder()
                .name(name)
                .done(done)
                .created(toInstant(created))
                .priority(Priority.valueOf(priority))
                .build();

        List<Task> tasks = taskRepository.findAll();
        Optional<Task> existingTask = tasks.stream()
                .filter(task -> task.equals(newTask))
                .findAny();
        return existingTask
                .map(task -> {
                    log.warn("Task already exists: task={}", task);
                    return ResponseEntity.badRequest().body("Task already exists");
                })
                .orElseGet(() -> ResponseEntity.ok(saveTask(name, done, created, priority)));
    }

    public ResponseEntity<String> updateTaskById(Long id, String name, boolean done, String created, String priority) {
        Task updatedTask = Task.builder()
                .name(name)
                .done(done)
                .created(toInstant(created))
                .priority(Priority.valueOf(priority))
                .build();
        return taskRepository.findById(id)
                .map(task -> {
                    BeanUtils.copyProperties(updatedTask, task, "id");
                    taskRepository.save(task);
                    log.info("Task updated successfully: id={}, updatedTask={}", id, updatedTask);
                    return ResponseEntity.ok("Task updated successfully");
                })
                .orElseGet(() -> ResponseEntity.badRequest().body("Task not found"));
    }

    public ResponseEntity<String> deleteTaskById(Long id) {
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

    private String saveTask(String name, Boolean done, String created, String priority) {
        taskRepository.save(Task.builder()
                .name(name)
                .done(done)
                .created(toInstant(created))
                .priority(Priority.valueOf(priority))
                .build());
        log.warn("Task created successfully: task={}", name);
        return "Task created successfully";
    }

    private Instant toInstant(String created) {
        try {
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .appendPattern("yyyy")
                    .optionalStart().appendPattern("-MM").optionalEnd()
                    .optionalStart().appendPattern("-dd").optionalEnd()
                    .optionalStart().appendPattern(" HH").optionalEnd()
                    .optionalStart().appendPattern(":mm").optionalEnd()
                    .optionalStart().appendPattern(":ss").optionalEnd()
                    .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
                    .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
                    .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                    .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                    .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                    .toFormatter();
            LocalDateTime localDateTime = LocalDateTime.parse(created, formatter);
            return localDateTime.toInstant(ZoneOffset.UTC);
        } catch (DateTimeParseException e) {
            log.error("Invalid date format: date={}", created);
            throw new IllegalArgumentException("Invalid date format. Please use 'yyyy-MM-dd HH:mm:ss' format.");
        }
    }
}
