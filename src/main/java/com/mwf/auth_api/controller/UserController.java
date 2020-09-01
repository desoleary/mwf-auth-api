package com.mwf.auth_api.controller;

import com.mwf.auth_api.payload.UserProfile;
import com.mwf.auth_api.security.CurrentUser;
import com.mwf.auth_api.security.UserPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

    @GetMapping("/users/me")
    @PreAuthorize("hasRole('USER')")
    public UserProfile getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        return new UserProfile(currentUser.getId(), currentUser.getName(), currentUser.getEmail());
    }
}
