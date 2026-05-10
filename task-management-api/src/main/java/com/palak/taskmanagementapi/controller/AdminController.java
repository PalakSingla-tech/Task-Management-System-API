package com.palak.taskmanagementapi.controller;

import com.palak.taskmanagementapi.dto.TaskResponseDTO;
import com.palak.taskmanagementapi.dto.UserResponseDTO;
import com.palak.taskmanagementapi.entity.Task;
import com.palak.taskmanagementapi.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "userId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search
    ) {
        log.info("Admin API: Fetching all users");
        return ResponseEntity.ok(adminService.getAllUsers(search, page, size, sortBy, sortDir));
    }

    @GetMapping("/tasks")
    public ResponseEntity<Page<TaskResponseDTO>> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "taskId") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Task.Status status,
            @RequestParam(required = false) Task.Priority priority,
            @RequestParam(required = false) Boolean deleted
    ) {
        log.info("Admin API: Fetching all tasks with filters - search: {}, status: {}, deleted: {}", search, status, deleted);
        return ResponseEntity.ok(adminService.getAllTasks(search, status, priority, deleted, page, size, sortBy, sortDir));
    }

    @PatchMapping("/tasks/{id}/restore")
    public ResponseEntity<String> restoreTask(@PathVariable Long id) {
        log.info("Admin API: Request to restore task {}", id);
        return ResponseEntity.ok(adminService.restoreTask(id));
    }

    @DeleteMapping("/tasks/{id}/permanent")
    public ResponseEntity<String> permanentlyDeleteTask(@PathVariable Long id) {
        log.info("Admin API: Request to permanently delete task {}", id);
        return ResponseEntity.ok(adminService.permanentlyDeleteTask(id));
    }
}
