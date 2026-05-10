package com.palak.taskmanagementapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;

    @NotNull(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Due Date is required")
    private LocalDate dueDate;

    public enum Status{
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        DELETED
    }

    public enum Priority{
        LOW,
        MEDIUM,
        HIGH
    }

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.LOW;

    @CreatedDate
    @Column(updatable = false)
    private LocalDate createdAt;

    @LastModifiedDate
    private LocalDate updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private boolean deleted = false;

}
