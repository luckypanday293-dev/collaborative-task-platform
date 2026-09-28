package com.lucky.collabtask.controller;

import com.lucky.collabtask.dto.ApiDtos.*;
import com.lucky.collabtask.model.*;
import com.lucky.collabtask.repository.CommentRepository;
import com.lucky.collabtask.service.*;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static com.lucky.collabtask.dto.ViewMapper.*;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {
    private final AssignmentService assignmentService;
    private final CommentRepository comments;
    private final CurrentUserService currentUser;
    private final ActivityService activityService;

    public AssignmentController(AssignmentService assignmentService, CommentRepository comments,
                                CurrentUserService currentUser, ActivityService activityService) {
        this.assignmentService = assignmentService;
        this.comments = comments;
        this.currentUser = currentUser;
        this.activityService = activityService;
    }

    @GetMapping
    public List<AssignmentView> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) AssignmentStatus status,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) String assignee) {
        return assignmentService.search(search, status, projectId, assignee).stream()
                .map(com.lucky.collabtask.dto.ViewMapper::assignment).toList();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public AssignmentView create(@Valid @RequestBody CreateAssignmentRequest request, Authentication authentication) {
        return assignment(assignmentService.create(request, currentUser.from(authentication)));
    }

    @PatchMapping("/{id}/status")
    public AssignmentView updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateStatusRequest request,
                                       Authentication authentication) {
        return assignment(assignmentService.updateStatus(id, request.status(), currentUser.from(authentication)));
    }

    @GetMapping("/{id}/comments")
    public List<CommentView> listComments(@PathVariable Long id) {
        assignmentService.get(id);
        return comments.findByAssignmentIdOrderByCreatedAtAsc(id).stream()
                .map(com.lucky.collabtask.dto.ViewMapper::comment).toList();
    }

    @PostMapping("/{id}/comments")
    public CommentView addComment(@PathVariable Long id, @Valid @RequestBody CreateCommentRequest request,
                                  Authentication authentication) {
        Assignment assignment = assignmentService.get(id);
        AppUser actor = currentUser.from(authentication);
        Comment saved = comments.save(new Comment(request.body(), assignment, actor));
        activityService.log("COMMENT_ADDED", "Commented on assignment: " + assignment.getTitle(),
                assignment.getProject(), actor);
        return comment(saved);
    }
}
