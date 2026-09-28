package com.nischal.collabtask.service;

import com.nischal.collabtask.model.Activity;
import com.nischal.collabtask.model.AppUser;
import com.nischal.collabtask.model.Project;
import com.nischal.collabtask.repository.ActivityRepository;
import org.springframework.stereotype.Service;

@Service
public class ActivityService {
    private final ActivityRepository activities;

    public ActivityService(ActivityRepository activities) { this.activities = activities; }

    public void log(String action, String details, Project project, AppUser actor) {
        activities.save(new Activity(action, details, project, actor));
    }
}
