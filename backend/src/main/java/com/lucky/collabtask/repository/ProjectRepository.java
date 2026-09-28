package com.lucky.collabtask.repository;

import com.lucky.collabtask.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
