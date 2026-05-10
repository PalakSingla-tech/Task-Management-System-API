package com.palak.taskmanagementapi.service;

import com.palak.taskmanagementapi.dto.TaskResponseDTO;
import com.palak.taskmanagementapi.dto.UserResponseDTO;
import com.palak.taskmanagementapi.entity.Task;
import com.palak.taskmanagementapi.entity.User;
import com.palak.taskmanagementapi.exception.ResourceNotFoundException;
import com.palak.taskmanagementapi.mapper.TaskMapper;
import com.palak.taskmanagementapi.mapper.UserMapper;
import com.palak.taskmanagementapi.repository.TaskRepository;
import com.palak.taskmanagementapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;
    private final UserMapper userMapper;

    public Page<UserResponseDTO> getAllUsers(String search, int page, int size, String sortBy, String sortDir) {
        log.info("Admin fetching all users with search: {}", search);
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<User> spec = (root, query, cb) -> cb.conjunction();

        if (search != null && !search.trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("username")), "%" + search.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("email")), "%" + search.toLowerCase() + "%")
            ));
        }

        return userRepository.findAll(spec, pageable).map(userMapper::mapToDTO);
    }

    public Page<TaskResponseDTO> getAllTasks(
            String search,
            Task.Status status,
            Task.Priority priority,
            Boolean deleted,
            int page,
            int size,
            String sortBy,
            String sortDir
    ) {
        log.info("Admin fetching all tasks with search: {}, status: {}, priority: {}, deleted: {}", search, status, priority, deleted);
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Task> spec = (root, query, cb) -> cb.conjunction();

        if (search != null && !search.trim().isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("title")), "%" + search.toLowerCase() + "%"));
        }
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (priority != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("priority"), priority));
        }
        if (deleted != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("deleted"), deleted));
        }

        return taskRepository.findAll(spec, pageable).map(taskMapper::mapToDTOSingle);
    }

    @Transactional
    public String restoreTask(Long id) {
        log.info("Admin restoring task ID: {}", id);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + id));

        if (!task.isDeleted()) {
            return "Task is not deleted";
        }

        task.setDeleted(false);
        taskRepository.save(task);
        return "Task restored successfully";
    }

    @Transactional
    public String permanentlyDeleteTask(Long id) {
        log.info("Admin permanently deleting task ID: {}", id);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + id));

        taskRepository.delete(task);
        return "Task permanently deleted";
    }
}
