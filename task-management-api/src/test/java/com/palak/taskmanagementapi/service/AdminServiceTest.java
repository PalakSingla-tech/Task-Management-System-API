package com.palak.taskmanagementapi.service;

import com.palak.taskmanagementapi.dto.TaskResponseDTO;
import com.palak.taskmanagementapi.dto.UserResponseDTO;
import com.palak.taskmanagementapi.entity.Task;
import com.palak.taskmanagementapi.entity.User;
import com.palak.taskmanagementapi.mapper.TaskMapper;
import com.palak.taskmanagementapi.mapper.UserMapper;
import com.palak.taskmanagementapi.repository.TaskRepository;
import com.palak.taskmanagementapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AdminService adminService;

    private User user;
    private Task task;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1L);
        user.setUsername("testuser");

        task = new Task();
        task.setTaskId(1L);
        task.setTitle("Admin Test Task");
        task.setDeleted(true);
    }

    @Test
    void testGetAllUsers_Success() {
        Page<User> page = new PageImpl<>(Collections.singletonList(user));
        when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(userMapper.mapToDTO(user)).thenReturn(new UserResponseDTO());

        Page<UserResponseDTO> result = adminService.getAllUsers(null, 0, 10, "userId", "asc");

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void testRestoreTask_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        String result = adminService.restoreTask(1L);

        assertEquals("Task restored successfully", result);
        assertFalse(task.isDeleted());
        verify(taskRepository).save(task);
    }

    @Test
    void testPermanentlyDeleteTask_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        String result = adminService.permanentlyDeleteTask(1L);

        assertEquals("Task permanently deleted", result);
        verify(taskRepository).delete(task);
    }
}
