package com.nischal.collabtask.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "activity_history")
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String action;

    @Column(nullable = false, length = 1000)
    private String details;

    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(optional = false)
    @JoinColumn(name = "actor_id", nullable = false)
    private AppUser actor;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public Activity() {}

    public Activity(String action, String details, Project project, AppUser actor) {
        this.action = action;
        this.details = details;
        this.project = project;
        this.actor = actor;
    }

    public Long getId() { return id; }
    public String getAction() { return action; }
    public String getDetails() { return details; }
    public Project getProject() { return project; }
    public AppUser getActor() { return actor; }
    public Instant getCreatedAt() { return createdAt; }
}
