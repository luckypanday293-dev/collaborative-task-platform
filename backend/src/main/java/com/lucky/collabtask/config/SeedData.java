package com.lucky.collabtask.config;

import com.lucky.collabtask.model.*;
import com.lucky.collabtask.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;

@Configuration
public class SeedData {
    @Bean
    CommandLineRunner seed(AppUserRepository users, ProjectRepository projects,
                           AssignmentRepository assignments, CommentRepository comments,
                           ActivityRepository activities, PasswordEncoder encoder) {
        return args -> {
            if (users.count() > 0) return;

            AppUser admin = users.save(new AppUser("admin", encoder.encode("admin123"), "Alex Admin", Role.ADMIN));
            AppUser member = users.save(new AppUser("member", encoder.encode("member123"), "Morgan Member", Role.MEMBER));

            Project project = projects.save(new Project(
                    "Website Launch",
                    "Coordinate engineering, content, QA, and release tasks for the public launch.", admin));

            Assignment a1 = assignments.save(new Assignment(
                    "Build project dashboard",
                    "Create the dashboard that summarizes projects, tasks, and recent activity.",
                    AssignmentStatus.IN_PROGRESS, Priority.HIGH, LocalDate.now().plusDays(5), project, member));
            assignments.save(new Assignment(
                    "Verify launch checklist",
                    "Review release requirements and mark completed validation items.",
                    AssignmentStatus.TODO, Priority.MEDIUM, LocalDate.now().plusDays(9), project, admin));

            comments.save(new Comment("Dashboard API is ready for frontend integration.", a1, admin));
            activities.save(new Activity("PROJECT_CREATED", "Created project: " + project.getTitle(), project, admin));
            activities.save(new Activity("ASSIGNMENT_CREATED", "Created assignment: " + a1.getTitle(), project, admin));
        };
    }
}
