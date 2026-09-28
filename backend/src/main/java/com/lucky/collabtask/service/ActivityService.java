package com.lucky.collabtask.service;

import com.lucky.collabtask.model.Activity;
import com.lucky.collabtask.model.AppUser;
import com.lucky.collabtask.model.Project;
import com.lucky.collabtask.repository.ActivityRepository;
import org.springframework.stereotype.Service;

@Service
public class ActivityService {
    private final ActivityRepository activities;

    public ActivityService(ActivityRepository activities) { this.activities = activities; }

    public void log(String action, String details, Project project, AppUser actor) {
        activities.save(new Activity(action, details, project, actor));
    }
}
