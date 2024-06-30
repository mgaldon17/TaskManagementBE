package com.taskmanagement.task_management_app.service;

import com.taskmanagement.task_management_app.model.Priority;
import com.taskmanagement.task_management_app.model.Task;
import com.taskmanagement.task_management_app.repository.TaskRepository;
import com.taskmanagement.task_management_app.rest.TaskController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }

    public HttpStatus createTask(String name, Boolean done, String created, String priority) {
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
                    return HttpStatus.BAD_REQUEST;
                })
                .orElseGet(() -> {
                    taskRepository.save(newTask);
                    log.warn("Task created successfully: task={}", newTask);
                    return HttpStatus.CREATED;
                });
    }

    public HttpStatus updateTaskById(Long id, String name, boolean done, String created, String priority) {
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
                    return HttpStatus.OK;
                })
                .orElseGet(() -> {
                    log.warn("Task not found: id={}", id);
                    return HttpStatus.NOT_FOUND;
                });
    }

    public HttpStatus deleteTaskById(Long id) {
        return taskRepository.findById(id)
                .map(task -> {
                    taskRepository.delete(task);
                    log.warn("Task deleted successfully: id={}", id);
                    return HttpStatus.OK;
                })
                .orElseGet(() -> {
                    log.warn("Task not found: id={}", id);
                    return HttpStatus.NOT_FOUND;
                });
    }

    private Instant toInstant(String created) {
        try {
            // If date is entered in format 2022-01-01T00:00:00Z, replace T and Z with space if there are any

            created = (created.contains("T") && created.contains("Z"))
                    ? created.replace("T", " ").replace("Z", "")
                    : (created.contains("."))
                    ? created.substring(0, created.indexOf("."))
                    : created;

            created = created.contains(".") ? created.substring(0, created.indexOf(".")) : created;

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
