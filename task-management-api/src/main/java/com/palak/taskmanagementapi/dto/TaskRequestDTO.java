package com.palak.taskmanagementapi.dto;

import com.palak.taskmanagementapi.entity.Task;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskRequestDTO {
    @NotNull(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Due Date is required")
    private LocalDate dueDate;

    private Task.Status status;
    private Task.Priority priority;
}
