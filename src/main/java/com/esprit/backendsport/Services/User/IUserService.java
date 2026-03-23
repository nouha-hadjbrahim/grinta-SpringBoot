package com.esprit.backendsport.Services.User;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.esprit.backendsport.DTOs.UpdateUserRequest;
import com.esprit.backendsport.Entities.User.User;
import org.springframework.web.multipart.MultipartFile;

public interface IUserService {
    User save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    List<User> findAll();
    void deleteById(Long id);
    
    /**
     * Update user with optional image upload
     * @param id User ID
     * @param updateRequest Update user details
     * @param imageFile Optional image file
     * @return Updated user
     * @throws IOException if file upload fails
     */
    User updateUser(Long id, UpdateUserRequest updateRequest, MultipartFile imageFile) throws IOException;
}
