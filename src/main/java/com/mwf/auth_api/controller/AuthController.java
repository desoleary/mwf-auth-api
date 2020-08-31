package com.mwf.auth_api.controller;

import com.mwf.auth_api.model.User;
import com.mwf.auth_api.payload.ApiResponse;
import com.mwf.auth_api.payload.SignUpRequest;
import com.mwf.auth_api.repository.UserRepository;
import com.mwf.auth_api.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtTokenProvider tokenProvider;

    @PostMapping("/signup_step_one")
    public ResponseEntity<?> signUpUserStepOne(@Valid @RequestBody SignUpRequest signUpRequest) {
        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity(new ApiResponse(false, "Email Address already in use!"),
                    HttpStatus.BAD_REQUEST);
        }

        User user = new User(signUpRequest.getEmail());

        User result = userRepository.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/signup_step_two/{id}")
                .buildAndExpand(result.getId()).toUri();


        return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));

    }
}
