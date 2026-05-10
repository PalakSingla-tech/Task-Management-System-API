package com.palak.taskmanagementapi.mapper;

import com.palak.taskmanagementapi.dto.CreateTaskDTO;
import com.palak.taskmanagementapi.dto.TaskResponseDTO;
import com.palak.taskmanagementapi.entity.Task;
import com.palak.taskmanagementapi.entity.User;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaskMapper {
    public Task mapToEntity(CreateTaskDTO createTaskDTO, User user){
        return Task.builder()
                .title(createTaskDTO.getTitle())
                .description(createTaskDTO.getDescription())
                .dueDate(createTaskDTO.getDueDate())
                .status(createTaskDTO.getStatus() != null ? createTaskDTO.getStatus() : Task.Status.PENDING)
                .priority(createTaskDTO.getPriority() != null ? createTaskDTO.getPriority() : Task.Priority.LOW)
                .user(user)
                .build();
    }

    public List<TaskResponseDTO> mapToDTO(List<Task> taskList)
    {
        return taskList.stream().map(this::mapToDTOSingle).collect(Collectors.toList());
    }

    public TaskResponseDTO mapToDTOSingle(Task task)
    {
        return TaskResponseDTO.builder()
                .taskId(task.getTaskId())
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDate(task.getDueDate())
                .status(task.getStatus())
                .priority(task.getPriority())
                .userId(task.getUser().getUserId())
                .build();
    }
}
