package com.palak.taskmanagementapi.repository;

import com.palak.taskmanagementapi.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    List<Task> findByStatus(String status);
    List<Task> findByTitleContaining(String taskTitle);
    List<Task> findByUser_UserId(Long userId);
}
