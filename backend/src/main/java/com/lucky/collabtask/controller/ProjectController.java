package com.lucky.collabtask.controller;

import com.lucky.collabtask.dto.ApiDtos.*;
import com.lucky.collabtask.exception.NotFoundException;
import com.lucky.collabtask.model.*;
import com.lucky.collabtask.repository.*;
import com.lucky.collabtask.service.*;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static com.lucky.collabtask.dto.ViewMapper.*;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectRepository projects;
    private final ActivityRepository activities;
    private final CurrentUserService currentUser;
    private final ActivityService activityService;

    public ProjectController(ProjectRepository projects, ActivityRepository activities,
                             CurrentUserService currentUser, ActivityService activityService) {
        this.projects = projects;
        this.activities = activities;
        this.currentUser = currentUser;
        this.activityService = activityService;
    }

    @GetMapping
    public List<ProjectView> list() {
        return projects.findAll().stream().map(com.lucky.collabtask.dto.ViewMapper::project).toList();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ProjectView create(@Valid @RequestBody CreateProjectRequest request, Authentication authentication) {
        AppUser actor = currentUser.from(authentication);
        Project saved = projects.save(new Project(request.title(), request.description(), actor));
        activityService.log("PROJECT_CREATED", "Created project: " + saved.getTitle(), saved, actor);
        return project(saved);
    }

    @GetMapping("/{projectId}/activity")
    public List<ActivityView> activity(@PathVariable Long projectId) {
        if (!projects.existsById(projectId)) throw new NotFoundException("Project not found");
        return activities.findTop50ByProjectIdOrderByCreatedAtDesc(projectId).stream()
                .map(com.lucky.collabtask.dto.ViewMapper::activity).toList();
    }
}
