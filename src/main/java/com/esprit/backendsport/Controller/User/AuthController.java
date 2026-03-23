package com.esprit.backendsport.Controller.User;

import com.esprit.backendsport.DTOs.AuthResponse;
import com.esprit.backendsport.DTOs.LoginRequest;
import com.esprit.backendsport.DTOs.RegisterRequest;
import com.esprit.backendsport.Services.Auth.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final AuthService authService;

    /**
     * Login endpoint
     * @param loginRequest containing email and password
     * @return AuthResponse with JWT token (200 OK)
     */
    @PostMapping("/sign-in")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.authenticate(loginRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Register endpoint
     * @param registerRequest containing name, email, phoneNumber, password, confirmPassword
     * @return AuthResponse with JWT token (201 CREATED) - auto-login after registration
     * @throws InvalidRequestException (400) - validation errors or password mismatch
     * @throws ResourceAlreadyExistsException (409) - email already exists
     */
    @PostMapping("/sign-up")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        AuthResponse response = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

