package com.palak.taskmanagementapi.service;

import com.palak.taskmanagementapi.dto.CreateTaskDTO;
import com.palak.taskmanagementapi.dto.TaskRequestDTO;
import com.palak.taskmanagementapi.entity.User;
import com.palak.taskmanagementapi.mapper.TaskMapper;
import com.palak.taskmanagementapi.dto.TaskResponseDTO;
import com.palak.taskmanagementapi.entity.Task;
import com.palak.taskmanagementapi.exception.ResourceNotFoundException;
import com.palak.taskmanagementapi.repository.TaskRepository;
import com.palak.taskmanagementapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserRepository userRepository;

    public Page<TaskResponseDTO> getAllTasks(Long userId, String search, Task.Status status, Task.Priority priority, int page, int size, String sortBy, String sortDir) {
        log.info("Fetching tasks for user ID: {} with search: {}, status: {}, priority: {}", userId, search, status, priority);
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Task> spec = Specification.<Task>where(
                (root, query, cb) -> cb.equal(root.get("user").get("userId"), userId)
        ).and(
                (root, query, cb) -> cb.isFalse(root.get("deleted"))
        );

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

        return taskRepository.findAll(spec, pageable).map(taskMapper::mapToDTOSingle);
    }

    public TaskResponseDTO getTaskById(Long id, Long userId) {
        log.info("Fetching task ID: {} for user ID: {}", id, userId);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + id));

        if (!task.getUser().getUserId().equals(userId)) {
            log.error("Unauthorized access attempt: User {} tried to access task {}", userId, id);
            throw new AccessDeniedException("Unauthorized access to this task");
        }

        if (task.isDeleted()) {
            log.warn("Attempted to access soft-deleted task ID: {}", id);
            throw new ResourceNotFoundException("Task not found with ID: " + id);
        }

        return taskMapper.mapToDTOSingle(task);
    }

    @Transactional
    public String deleteTaskById(Long id, Long userId) {
        log.info("Soft-deleting task ID: {} for user ID: {}", id, userId);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + id));

        if (!task.getUser().getUserId().equals(userId)) {
            log.error("Unauthorized delete attempt: User {} tried to delete task {}", userId, id);
            throw new AccessDeniedException("Unauthorized to delete this task");
        }

        if (task.isDeleted()) {
            return "Task already deleted";
        }

        task.setDeleted(true);
        taskRepository.save(task);

        return "Task moved to trash successfully";
    }

    @Transactional
    public String markTaskAsComplete(Long id, Long userId) {
        log.info("Marking task ID: {} as complete for user ID: {}", id, userId);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + id));

        if (!task.getUser().getUserId().equals(userId)) {
            log.error("Unauthorized update attempt: User {} tried to complete task {}", userId, id);
            throw new AccessDeniedException("Unauthorized to update this task");
        }

        if (task.isDeleted()) {
            throw new ResourceNotFoundException("Task not found with ID: " + id);
        }

        task.setStatus(Task.Status.COMPLETED);
        taskRepository.save(task);

        return "Task marked as completed";
    }

    @Transactional
    public String createTask(CreateTaskDTO createTaskDTO, Long userId) {
        log.info("Creating new task for user ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        Task task = taskMapper.mapToEntity(createTaskDTO, user);
        taskRepository.save(task);
        return "Task Created successfully";
    }

    @Transactional
    public TaskResponseDTO updateTask(Long id, TaskRequestDTO taskRequestDTO, Long userId) {
        log.info("Updating task ID: {} for user ID: {}", id, userId);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + id));

        if (task.isDeleted()) {
            throw new ResourceNotFoundException("Task not found with ID: " + id);
        }

        if (!task.getUser().getUserId().equals(userId)) {
            log.error("Unauthorized update attempt: User {} tried to update task {}", userId, id);
            throw new AccessDeniedException("Unauthorized to update this task");
        }

        task.setTitle(taskRequestDTO.getTitle());
        task.setDescription(taskRequestDTO.getDescription());
        task.setDueDate(taskRequestDTO.getDueDate());
        task.setStatus(taskRequestDTO.getStatus());
        task.setPriority(taskRequestDTO.getPriority());

        Task updatedTask = taskRepository.save(task);
        return taskMapper.mapToDTOSingle(updatedTask);
    }
}
