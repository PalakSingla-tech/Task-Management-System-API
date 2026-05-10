package com.palak.taskmanagementapi.dto;

import com.palak.taskmanagementapi.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTaskDTO {
    private String title;
    private String description;
    private LocalDate dueDate;
    private Task.Status status;
    private Task.Priority priority;
}
