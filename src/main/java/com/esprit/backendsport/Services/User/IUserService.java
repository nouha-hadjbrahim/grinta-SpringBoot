package com.esprit.backendsport.Services.User;

import java.util.List;
import java.util.Optional;

import com.esprit.backendsport.Entities.User.User;

public interface IUserService {
    User save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    List<User> findAll();
    void deleteById(Long id);
}
