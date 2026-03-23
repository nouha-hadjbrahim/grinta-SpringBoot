package com.esprit.backendsport.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Email(message = "Email should be valid")
    private String email;

    @Pattern(regexp = "^[0-9\\-\\+\\s\\(\\)]{6,15}$", message = "Phone number must be valid (6-15 characters)")
    private String phoneNumber;

    private String role;

    private String status;

    // Note: image/photo is handled separately via MultipartFile in controller
}
