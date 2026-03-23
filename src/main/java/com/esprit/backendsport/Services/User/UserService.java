package com.esprit.backendsport.Services.User;

import com.esprit.backendsport.DTOs.UpdateUserRequest;
import com.esprit.backendsport.Entities.User.User;
import com.esprit.backendsport.Repository.User.UserRepository;
import com.esprit.backendsport.Services.File.FileUploadService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        // Delete old photo if exists
        Optional<User> user = findById(id);
        if (user.isPresent() && user.get().getPhoto() != null) {
            fileUploadService.deleteFile(user.get().getPhoto());
        }
        userRepository.deleteById(id);
    }

    /**
     * Update user with optional image upload
     * If image is provided: saves it and updates photo field
     * If no image: keeps existing photo
     * 
     * @param id User ID
     * @param updateRequest Update user details
     * @param imageFile Optional image file
     * @return Updated user
     * @throws IOException if file upload fails
     */
    @Override
    public User updateUser(Long id, UpdateUserRequest updateRequest, MultipartFile imageFile) throws IOException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        // Update basic fields
        if (updateRequest.getName() != null && !updateRequest.getName().isEmpty()) {
            user.setName(updateRequest.getName());
        }

        if (updateRequest.getEmail() != null && !updateRequest.getEmail().isEmpty()) {
            // Check if email is already taken by another user
            Optional<User> existingUser = userRepository.findByEmail(updateRequest.getEmail());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
                throw new IllegalArgumentException("Email is already in use");
            }
            user.setEmail(updateRequest.getEmail());
        }

        if (updateRequest.getPhoneNumber() != null && !updateRequest.getPhoneNumber().isEmpty()) {
            user.setPhoneNumber(updateRequest.getPhoneNumber());
        }

        if (updateRequest.getRole() != null && !updateRequest.getRole().isEmpty()) {
            user.setRole(com.esprit.backendsport.Entities.User.Role.valueOf(updateRequest.getRole()));
        }

        if (updateRequest.getStatus() != null && !updateRequest.getStatus().isEmpty()) {
            user.setStatus(com.esprit.backendsport.Entities.User.Status.valueOf(updateRequest.getStatus()));
        }

        // Handle image upload
        if (imageFile != null && !imageFile.isEmpty()) {
            // Delete old photo if exists
            if (user.getPhoto() != null) {
                fileUploadService.deleteFile(user.getPhoto());
            }

            // Save new photo
            String fileName = fileUploadService.saveFile(imageFile);
            user.setPhoto(fileName);
        }

        return userRepository.save(user);
    }
}
