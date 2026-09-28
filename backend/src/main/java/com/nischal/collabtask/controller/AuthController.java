package com.nischal.collabtask.controller;

import com.nischal.collabtask.dto.ApiDtos.UserView;
import com.nischal.collabtask.service.CurrentUserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import static com.nischal.collabtask.dto.ViewMapper.user;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final CurrentUserService currentUser;

    public AuthController(CurrentUserService currentUser) { this.currentUser = currentUser; }

    @GetMapping("/me")
    public UserView me(Authentication authentication) {
        return user(currentUser.from(authentication));
    }
}
