package com.esprit.backendsport.Services.Auth;

import com.esprit.backendsport.DTOs.AuthResponse;
import com.esprit.backendsport.DTOs.LoginRequest;
import com.esprit.backendsport.DTOs.RegisterRequest;
import com.esprit.backendsport.Entities.User.Role;
import com.esprit.backendsport.Entities.User.User;
import com.esprit.backendsport.Exception.InvalidRequestException;
import com.esprit.backendsport.Exception.ResourceAlreadyExistsException;
import com.esprit.backendsport.Security.JwtUtil;
import com.esprit.backendsport.Services.User.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final IUserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    /**
     * Authenticate user with email and password
     * @param loginRequest containing email and password
     * @return AuthResponse with JWT token and user details
     */
    public AuthResponse authenticate(LoginRequest loginRequest) {
        Optional<User> userOpt = userService.findByEmail(loginRequest.getEmail());

        if (userOpt.isEmpty() || !passwordEncoder.matches(loginRequest.getPassword(), userOpt.get().getPassword())) {
            throw new InvalidRequestException("Invalid email or password");
        }

        User user = userOpt.get();
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return AuthResponse.builder()
                .token(token)
                .user(AuthResponse.UserDto.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .phoneNumber(user.getPhoneNumber())
                        .role(user.getRole())
                        .build())
                .build();
    }

    /**
     * Register new user with validation
     * @param registerRequest containing user registration details
     * @return AuthResponse with JWT token and user details (auto-login)
     */
    public AuthResponse register(RegisterRequest registerRequest) {
        // Validate password confirmation
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new InvalidRequestException("Password and confirm password do not match");
        }

        // Check if email already exists
        if (userService.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("Email already registered");
        }

        // Create new user
        User user = User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .phoneNumber(registerRequest.getPhoneNumber())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.USER)  // Default role
                .status(com.esprit.backendsport.Entities.User.Status.ACTIVE)  // Default status
                .photo("assets/default.png")  // Default photo
                .build();

        // Save user to database
        User savedUser = userService.save(user);

        // Generate JWT token (auto-login)
        String token = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getRole());

        return AuthResponse.builder()
                .token(token)
                .user(AuthResponse.UserDto.builder()
                        .id(savedUser.getId())
                        .name(savedUser.getName())
                        .email(savedUser.getEmail())
                        .phoneNumber(savedUser.getPhoneNumber())
                        .role(savedUser.getRole())
                        .build())
                .build();
    }
}
