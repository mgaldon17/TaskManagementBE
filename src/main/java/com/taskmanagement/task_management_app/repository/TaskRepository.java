package com.taskmanagement.task_management_app.repository;

import com.taskmanagement.task_management_app.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository  extends JpaRepository<Task, Long> {

}
