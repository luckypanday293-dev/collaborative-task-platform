package com.lucky.collabtask.dto;

import com.lucky.collabtask.model.AssignmentStatus;
import com.lucky.collabtask.model.Priority;
import com.lucky.collabtask.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDate;

public final class ApiDtos {
    private ApiDtos() {}

    public record UserView(Long id, String username, String displayName, Role role) {}

    public record ProjectView(Long id, String title, String description, UserView owner, Instant createdAt) {}

    public record CreateProjectRequest(
            @NotBlank @Size(max = 120) String title,
            @NotBlank @Size(max = 1000) String description) {}

    public record AssignmentView(
            Long id,
            String title,
            String description,
            AssignmentStatus status,
            Priority priority,
            LocalDate dueDate,
            Long projectId,
            String projectTitle,
            UserView assignee,
            Instant createdAt,
            Instant updatedAt) {}

    public record CreateAssignmentRequest(
            @NotBlank @Size(max = 160) String title,
            @NotBlank @Size(max = 2000) String description,
            AssignmentStatus status,
            Priority priority,
            LocalDate dueDate,
            @NotNull Long projectId,
            String assigneeUsername) {}

    public record UpdateStatusRequest(@NotNull AssignmentStatus status) {}

    public record CommentView(Long id, String body, UserView author, Instant createdAt) {}

    public record CreateCommentRequest(@NotBlank @Size(max = 2000) String body) {}

    public record ActivityView(Long id, String action, String details, UserView actor, Instant createdAt) {}
}
