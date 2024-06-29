package com.taskmanagement.task_management_app.rest;

import com.taskmanagement.task_management_app.model.Task;
import com.taskmanagement.task_management_app.service.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = TaskController.class,  excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;
    @Autowired
    WebApplicationContext context;

    @Value("${app.security.user}")
    private String user;

    @Value("${app.security.password}")
    private String password;

    @Value("${app.security.test}")
    private String auth;

    @Test
    @DisplayName("Get all tasks")
    public void getAllTasksTest() throws Exception {

        List<Task> tasks = Arrays.asList(Task
                .builder()
                .name("Test Task")
                .build());

        when(taskService.getAllTasks()).thenReturn(tasks);

        mockMvc.perform(get("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"name\": \"Test Task\"}]"));

        verify(taskService, times(1)).getAllTasks();
    }

    @Test
    @DisplayName("Create new task")
    public void createNewTaskTest() throws Exception {

        when(taskService.createTask(anyString(), anyBoolean(), anyString(), anyString()))
                .thenReturn(HttpStatus.OK);

        mockMvc.perform(post("/tasks")
                        .param("name", "Test Task")
                        .param("done", String.valueOf(false))
                        .param("created", "2022-01-01 00:00:00")
                        .param("priority", "URGENT")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(taskService, times(1))
                .createTask(anyString(), anyBoolean(), anyString(), anyString());
    }

    @Test
    @DisplayName("Update task by id")
    public void updateTaskByIdTest() throws Exception {
        when(taskService.updateTaskById(anyLong(), anyString(), anyBoolean(), anyString(), anyString()))
                .thenReturn(HttpStatus.OK);

        mockMvc.perform(put("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("name", "Test 2")
                        .param("done", String.valueOf(false))
                        .param("created", "2022-01-01 00:00:00")
                        .param("priority", "URGENT"))
                .andExpect(status().isOk());

        verify(taskService, times(1))
                .updateTaskById(anyLong(), anyString(), anyBoolean(), anyString(), anyString());
    }

    @Test
    @DisplayName("Delete task by id")
    public void deleteTaskByIdTest() throws Exception {
        when(taskService.deleteTaskById(anyLong()))
                .thenReturn(HttpStatus.OK);

        mockMvc.perform(delete("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(taskService, times(1))
                .deleteTaskById(anyLong());
    }
}
