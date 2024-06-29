package com.taskmanagement.task_management_app.service;

import com.taskmanagement.task_management_app.model.Priority;
import com.taskmanagement.task_management_app.model.Task;
import com.taskmanagement.task_management_app.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task());
        when(taskRepository.findAll()).thenReturn(tasks);

        List<Task> result = taskService.getAllTasks();
        assertEquals(1, result.size());
    }

    @Test
    public void testGetTaskById() {
        Task task = new Task();
        task.setId(1L);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Task result = taskService.getTaskById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    public void testCreateTaskSuccess() {
        when(taskRepository.findAll()).thenReturn(new ArrayList<>());
        when(taskRepository.save(any(Task.class))).thenReturn(new Task());

        HttpStatus result = taskService.createTask("Test Task", false, "2023-01-01 00:00:00", "LOW");
        assertEquals(HttpStatus.CREATED, result);
    }

    @Test
    public void testCreateTaskAlreadyExists() {
        Task task = Task.builder()
                .name("Test Task")
                .done(false)
                .created(Instant.parse("2023-01-01T00:00:00Z"))
                .priority(Priority.LOW)
                .build();

        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
        when(taskRepository.findAll()).thenReturn(tasks);

        HttpStatus result = taskService.createTask("Test Task", false, "2023-01-01 00:00:00", "LOW");
        assertEquals(HttpStatus.BAD_REQUEST, result);
    }

    @Test
    public void testUpdateTaskByIdSuccess() {
        Task task = new Task();
        task.setId(1L);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(new Task());

        HttpStatus result = taskService.updateTaskById(1L, "Updated Task", true, "2023-01-01 00:00:00", "URGENT");
        assertEquals(HttpStatus.OK, result);
    }

    @Test
    public void testUpdateTaskByIdNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        HttpStatus result = taskService.updateTaskById(1L, "Updated Task", true, "2023-01-01 00:00:00", "URGENT");
        assertEquals(HttpStatus.NOT_FOUND, result);
    }

    @Test
    public void testDeleteTaskByIdSuccess() {
        Task task = new Task();
        task.setId(1L);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        HttpStatus result = taskService.deleteTaskById(1L);
        assertEquals(HttpStatus.OK, result);
        verify(taskRepository, times(1)).delete(task);
    }

    @Test
    public void testDeleteTaskByIdNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        HttpStatus result = taskService.deleteTaskById(1L);
        assertEquals(HttpStatus.NOT_FOUND, result);
    }
}
