package com.lucky.collabtask.service;

import com.lucky.collabtask.dto.ApiDtos.CreateAssignmentRequest;
import com.lucky.collabtask.exception.NotFoundException;
import com.lucky.collabtask.model.*;
import com.lucky.collabtask.repository.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AssignmentService {
    private final AssignmentRepository assignments;
    private final ProjectRepository projects;
    private final AppUserRepository users;
    private final ActivityService activityService;

    public AssignmentService(AssignmentRepository assignments, ProjectRepository projects,
                             AppUserRepository users, ActivityService activityService) {
        this.assignments = assignments;
        this.projects = projects;
        this.users = users;
        this.activityService = activityService;
    }

    @Transactional(readOnly = true)
    public List<Assignment> search(String search, AssignmentStatus status, Long projectId, String assignee) {
        Specification<Assignment> spec = (root, query, cb) -> cb.conjunction();
        if (search != null && !search.isBlank()) {
            String term = "%" + search.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("title")), term),
                    cb.like(cb.lower(root.get("description")), term)));
        }
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (projectId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("project").get("id"), projectId));
        }
        if (assignee != null && !assignee.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("assignee").get("username"), assignee));
        }
        return assignments.findAll(spec, org.springframework.data.domain.Sort.by("updatedAt").descending());
    }

    @Transactional
    public Assignment create(CreateAssignmentRequest request, AppUser actor) {
        Project project = projects.findById(request.projectId())
                .orElseThrow(() -> new NotFoundException("Project not found"));
        AppUser assignee = null;
        if (request.assigneeUsername() != null && !request.assigneeUsername().isBlank()) {
            assignee = users.findByUsername(request.assigneeUsername().trim())
                    .orElseThrow(() -> new NotFoundException("Assignee not found"));
        }
        Assignment assignment = new Assignment(
                request.title(), request.description(),
                request.status() == null ? AssignmentStatus.TODO : request.status(),
                request.priority() == null ? Priority.MEDIUM : request.priority(),
                request.dueDate(), project, assignee);
        Assignment saved = assignments.save(assignment);
        activityService.log("ASSIGNMENT_CREATED", "Created assignment: " + saved.getTitle(), project, actor);
        return saved;
    }

    @Transactional
    public Assignment updateStatus(Long id, AssignmentStatus status, AppUser actor) {
        Assignment assignment = assignments.findById(id)
                .orElseThrow(() -> new NotFoundException("Assignment not found"));
        AssignmentStatus old = assignment.getStatus();
        assignment.setStatus(status);
        Assignment saved = assignments.save(assignment);
        activityService.log("STATUS_CHANGED", saved.getTitle() + ": " + old + " → " + status,
                saved.getProject(), actor);
        return saved;
    }

    @Transactional(readOnly = true)
    public Assignment get(Long id) {
        return assignments.findById(id).orElseThrow(() -> new NotFoundException("Assignment not found"));
    }
}
