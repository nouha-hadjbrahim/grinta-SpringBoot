package com.esprit.backendsport.Controller.User;

import com.esprit.backendsport.DTOs.ApiResponse;
import com.esprit.backendsport.DTOs.UpdateUserRequest;
import com.esprit.backendsport.Entities.User.User;
import com.esprit.backendsport.Services.User.IUserService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final IUserService userService;

    /**
     * Get current authenticated user profile
     * Accessible by: USER, ADMIN
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        String email = (String) authentication.getPrincipal();
        Optional<User> user = userService.findByEmail(email);
        
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Update user profile
     * Accessible by: USER, ADMIN
     */
    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(@RequestBody User user, Authentication authentication) {
        String email = (String) authentication.getPrincipal();
        Optional<User> existingUser = userService.findByEmail(email);

        if (existingUser.isPresent()) {
            User userToUpdate = existingUser.get();
            // Prevent email and role change
            user.setId(userToUpdate.getId());
            user.setEmail(userToUpdate.getEmail());
            user.setRole(userToUpdate.getRole());

            return ResponseEntity.ok(userService.save(user));
        }

        return ResponseEntity.notFound().build();
    }

    /**
     * Update user with optional image upload
     * Endpoint: PUT /api/user/users/{id}
     * Accept: name, email, phone, role, status, image (optional MultipartFile)
     * 
     * Accessible by: ADMIN or the user updating their own profile
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @ModelAttribute UpdateUserRequest updateRequest,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            @RequestParam(value = "file", required = false) MultipartFile fileUpload,
            Authentication authentication) {

        try {
            // Accept either "image" or "file" field name
            MultipartFile uploadedFile = (imageFile != null) ? imageFile : fileUpload;
            User updatedUser = userService.updateUser(id, updateRequest, uploadedFile);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            // Handle user not found or validation errors
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.builder()
                        .statusCode(404)
                        .message(e.getMessage())
                        .success(false)
                        .build()
                );
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponse.builder()
                        .statusCode(400)
                        .message(e.getMessage())
                        .success(false)
                        .build()
                );
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.builder()
                    .statusCode(500)
                    .message("File upload failed: " + e.getMessage())
                    .success(false)
                    .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.builder()
                    .statusCode(500)
                    .message("Internal server error: " + e.getMessage())
                    .success(false)
                    .build()
            );
        }
    }

    /**
     * Get all users
     * Endpoint: GET /api/user/users
     * Return: List<User>
     */
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    /**
     * Get user by ID
     * Endpoint: GET /api/user/users/{id}
     * Return: User or 404 with message "User not found"
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        }
        
        return ResponseEntity.status(404).body(
            ApiResponse.builder()
                .statusCode(404)
                .message("User not found")
                .success(false)
                .build()
        );
    }

    /**
     * Delete user by ID
     * Endpoint: DELETE /api/user/users/{id}
     * Return: Success message or 404 if user doesn't exist
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        
        if (user.isPresent()) {
            userService.deleteById(id);
            return ResponseEntity.ok(
                ApiResponse.builder()
                    .statusCode(200)
                    .message("User deleted successfully")
                    .success(true)
                    .build()
            );
        }
        
        return ResponseEntity.status(404).body(
            ApiResponse.builder()
                .statusCode(404)
                .message("User not found")
                .success(false)
                .build()
        );
    }

    /**
     * Get user by email
     * Accessible by: Any authenticated user (from original implementation)
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getByEmail(@PathVariable String email) {
        return userService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get user by ID (Admin endpoint)
     * Accessible by: ADMIN only
     */
    @GetMapping("/admin/users/{id}")
    public ResponseEntity<User> getAdminUserById(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Delete user by ID (Admin endpoint)
     * Accessible by: ADMIN only
     */
    @DeleteMapping("/admin/users/{id}")
    public ResponseEntity<Void> deleteAdminUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
