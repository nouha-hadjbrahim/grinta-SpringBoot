package com.esprit.backendsport.Controller.Admin;

import com.esprit.backendsport.Entities.User.Role;
import com.esprit.backendsport.Entities.User.User;
import com.esprit.backendsport.Services.User.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    private final IUserService userService;

    /**
     * Get dashboard statistics
     * Accessible by: ADMIN only
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard() {
        return ResponseEntity.ok(new Object() {
            public final int totalUsers = (int) userService.findAll().size();
            public final int adminUsers = (int) userService.findAll().stream()
                    .filter(u -> u.getRole() == Role.ADMIN).count();
            public final int regularUsers = (int) userService.findAll().stream()
                    .filter(u -> u.getRole() == Role.USER).count();
        });
    }

    /**
     * Get all users
     * Accessible by: ADMIN only
     */
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    /**
     * Get user by ID
     * Accessible by: ADMIN only
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Update user role
     * Accessible by: ADMIN only
     */
    @PutMapping("/users/{id}/role")
    public ResponseEntity<User> updateUserRole(@PathVariable Long id, @RequestParam Role role) {
        Optional<User> userOpt = userService.findById(id);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setRole(role);
            return ResponseEntity.ok(userService.save(user));
        }
        
        return ResponseEntity.notFound().build();
    }

    /**
     * Delete user
     * Accessible by: ADMIN only
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get user statistics
     * Accessible by: ADMIN only
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics() {
        List<User> allUsers = userService.findAll();
        return ResponseEntity.ok(new Object() {
            public final int total = allUsers.size();
            public final int admins = (int) allUsers.stream().filter(u -> u.getRole() == Role.ADMIN).count();
            public final int users = (int) allUsers.stream().filter(u -> u.getRole() == Role.USER).count();
        });
    }
}
