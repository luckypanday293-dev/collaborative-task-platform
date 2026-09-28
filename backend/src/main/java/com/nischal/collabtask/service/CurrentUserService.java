package com.nischal.collabtask.service;

import com.nischal.collabtask.exception.NotFoundException;
import com.nischal.collabtask.model.AppUser;
import com.nischal.collabtask.repository.AppUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {
    private final AppUserRepository users;

    public CurrentUserService(AppUserRepository users) { this.users = users; }

    public AppUser from(Authentication authentication) {
        return users.findByUsername(authentication.getName())
                .orElseThrow(() -> new NotFoundException("Authenticated user no longer exists"));
    }
}
