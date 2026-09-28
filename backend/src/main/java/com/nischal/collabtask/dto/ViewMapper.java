package com.nischal.collabtask.dto;

import com.nischal.collabtask.model.*;
import static com.nischal.collabtask.dto.ApiDtos.*;

public final class ViewMapper {
    private ViewMapper() {}

    public static UserView user(AppUser user) {
        if (user == null) return null;
        return new UserView(user.getId(), user.getUsername(), user.getDisplayName(), user.getRole());
    }

    public static ProjectView project(Project project) {
        return new ProjectView(project.getId(), project.getTitle(), project.getDescription(),
                user(project.getOwner()), project.getCreatedAt());
    }

    public static AssignmentView assignment(Assignment a) {
        return new AssignmentView(a.getId(), a.getTitle(), a.getDescription(), a.getStatus(), a.getPriority(),
                a.getDueDate(), a.getProject().getId(), a.getProject().getTitle(), user(a.getAssignee()),
                a.getCreatedAt(), a.getUpdatedAt());
    }

    public static CommentView comment(Comment c) {
        return new CommentView(c.getId(), c.getBody(), user(c.getAuthor()), c.getCreatedAt());
    }

    public static ActivityView activity(Activity a) {
        return new ActivityView(a.getId(), a.getAction(), a.getDetails(), user(a.getActor()), a.getCreatedAt());
    }
}
