package com.palak.taskmanagementapi.service;

import com.palak.taskmanagementapi.dto.TaskResponseDTO;
import com.palak.taskmanagementapi.entity.Task;
import com.palak.taskmanagementapi.entity.User;
import com.palak.taskmanagementapi.exception.ResourceNotFoundException;
import com.palak.taskmanagementapi.mapper.TaskMapper;
import com.palak.taskmanagementapi.repository.TaskRepository;
import com.palak.taskmanagementapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    private User user;
    private Task task;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1L);
        user.setUsername("testuser");

        task = new Task();
        task.setTaskId(1L);
        task.setTitle("Test Task");
        task.setUser(user);
        task.setDeleted(false);
    }

    @Test
    void testGetTaskById_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskMapper.mapToDTOSingle(task)).thenReturn(new TaskResponseDTO());

        TaskResponseDTO result = taskService.getTaskById(1L, 1L);

        assertNotNull(result);
        verify(taskRepository).findById(1L);
    }

    @Test
    void testGetTaskById_Unauthorized() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThrows(AccessDeniedException.class, () -> {
            taskService.getTaskById(1L, 2L); // Different userId
        });
    }

    @Test
    void testGetTaskById_NotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.getTaskById(1L, 1L);
        });
    }

    @Test
    void testDeleteTaskById_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        String result = taskService.deleteTaskById(1L, 1L);

        assertEquals("Task moved to trash successfully", result);
        assertTrue(task.isDeleted());
        verify(taskRepository).save(task);
    }

    @Test
    void testGetAllTasks_Success() {
        Page<Task> page = new PageImpl<>(Collections.singletonList(task));
        when(taskRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(taskMapper.mapToDTOSingle(any())).thenReturn(new TaskResponseDTO());

        Page<TaskResponseDTO> result = taskService.getAllTasks(1L, null, null, null, 0, 10, "taskId", "desc");

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }
}
