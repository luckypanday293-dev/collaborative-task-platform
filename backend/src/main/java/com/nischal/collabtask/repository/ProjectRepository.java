package com.nischal.collabtask.repository;

import com.nischal.collabtask.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
