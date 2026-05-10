package com.palak.taskmanagementapi.controller;

import com.palak.taskmanagementapi.dto.CreateTaskDTO;
import com.palak.taskmanagementapi.dto.TaskRequestDTO;
import com.palak.taskmanagementapi.dto.TaskResponseDTO;
import com.palak.taskmanagementapi.entity.Task;
import com.palak.taskmanagementapi.entity.User;
import com.palak.taskmanagementapi.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tasks")
@Slf4j
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<Page<TaskResponseDTO>> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "taskId") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Task.Status status,
            @RequestParam(required = false) Task.Priority priority,
            Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        log.info("API request: Fetching all tasks for user: {}", user.getUsername());
        return ResponseEntity.ok(taskService.getAllTasks(user.getUserId(), search, status, priority, page, size, sortBy, sortDir));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable Long id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        log.info("API request: Fetching task {} for user: {}", id, user.getUsername());
        return ResponseEntity.ok(taskService.getTaskById(id, user.getUserId()));
    }

    @PostMapping
    public ResponseEntity<String> createTask(@Valid @RequestBody CreateTaskDTO createTaskDTO, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        log.info("API request: Creating new task for user: {}", user != null ? user.getUsername() : null);
        return ResponseEntity.ok(taskService.createTask(createTaskDTO, user != null ? user.getUserId() : null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequestDTO taskRequestDTO, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        log.info("API request: Updating task {} for user: {}", id, user != null ? user.getUsername() : null);
        return ResponseEntity.ok(taskService.updateTask(id, taskRequestDTO, user != null ? user.getUserId() : null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTaskById(@PathVariable Long id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        log.info("API request: Deleting task {} for user: {}", id, user != null ? user.getUsername() : null);
        return ResponseEntity.ok(taskService.deleteTaskById(id, user != null ? user.getUserId() : null));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<String> markTaskAsComplete(@PathVariable Long id, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        log.info("API request: Completing task {} for user: {}", id, user != null ? user.getUsername() : null);
        return ResponseEntity.ok(taskService.markTaskAsComplete(id, user != null ? user.getUserId() : null));
    }
}
