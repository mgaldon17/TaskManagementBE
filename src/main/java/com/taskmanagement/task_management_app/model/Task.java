package com.taskmanagement.task_management_app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.Instant;
import java.util.Objects;

@Entity
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "tasks", schema = "task_management")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Size(max = 255, message = "Name must be less than 255 characters")
    private String name;

    @NotNull
    private boolean done;

    private Instant created;

    @Enumerated(EnumType.STRING)
    public Priority priority;

    @Override
    public boolean equals(Object o) {
        return this == o
                || o != null && getClass() == o.getClass()
                && isEqualFields((Task) o);

    }
    private boolean isEqualFields (Task task){
        return done == task.done &&
                Objects.equals(name, task.name) &&
                Objects.equals(created, task.created) &&
                priority == task.priority;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
