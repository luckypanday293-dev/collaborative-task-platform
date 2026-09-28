package com.lucky.collabtask.repository;

import com.lucky.collabtask.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findTop50ByProjectIdOrderByCreatedAtDesc(Long projectId);
}
