package com.nischal.collabtask.repository;

import com.nischal.collabtask.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByAssignmentIdOrderByCreatedAtAsc(Long assignmentId);
}
