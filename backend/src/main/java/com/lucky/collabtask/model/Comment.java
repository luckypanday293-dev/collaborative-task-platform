package com.lucky.collabtask.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2000)
    private String body;

    @ManyToOne(optional = false)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private AppUser author;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public Comment() {}

    public Comment(String body, Assignment assignment, AppUser author) {
        this.body = body;
        this.assignment = assignment;
        this.author = author;
    }

    public Long getId() { return id; }
    public String getBody() { return body; }
    public Assignment getAssignment() { return assignment; }
    public AppUser getAuthor() { return author; }
    public Instant getCreatedAt() { return createdAt; }
}
