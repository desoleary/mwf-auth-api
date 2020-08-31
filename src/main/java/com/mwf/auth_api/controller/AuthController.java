package com.mwf.auth_api.controller;

import com.mwf.auth_api.model.User;
import com.mwf.auth_api.payload.*;
import com.mwf.auth_api.repository.UserRepository;
import com.mwf.auth_api.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtTokenProvider tokenProvider;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/signin")
    public ResponseEntity<?> jwtSignInUser(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(jwtSignInUser(loginRequest.getEmail(), loginRequest.getPassword()));
    }

    @PostMapping("/signup_step_one")
    public ResponseEntity<?> signUpUserStepOne(@Valid @RequestBody SignUpStepOneRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return new ResponseEntity(new ApiResponse(false, "Email Address already in use!"),
                    HttpStatus.BAD_REQUEST);
        }

        User user = new User(request.getName(), request.getEmail());

        User result = userRepository.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/signup_step_two/{id}")
                .buildAndExpand(result.getId()).toUri();


        return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));

    }

    @PostMapping("/signup_step_two")
    public ResponseEntity<?> signUpUserStepTwo(@Valid @RequestBody SignUpStepTwoRequest request) {
        Optional<User> optionalUser = userRepository.findById(request.getId());

        if (!optionalUser.isPresent()) {
            return new ResponseEntity(new ApiResponse(false, "User not found."),
                    HttpStatus.BAD_REQUEST);
        }

        if (!request.getPassword().equals(request.getConfirmationPassword())) {
            return new ResponseEntity(new ApiResponse(false, "Passwords must match exactly."),
                    HttpStatus.BAD_REQUEST);

        }

        User user = optionalUser.get();
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User result = userRepository.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/users/{id}")
                .buildAndExpand(result.getId()).toUri();

        jwtSignInUser(user.getEmail(), request.getPassword());

        return ResponseEntity.created(location).body(new ApiResponse(true, "User fully registered successfully"));

    }

    private JwtAuthenticationResponse jwtSignInUser(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        return new JwtAuthenticationResponse(jwt);
    }
}
