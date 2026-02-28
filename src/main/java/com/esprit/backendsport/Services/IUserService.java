package com.esprit.backendsport.Services;

import com.esprit.backendsport.Entities.User;
import java.util.List;
import java.util.Optional;

public interface IUserService {
    User save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    List<User> findAll();
    void deleteById(Long id);
}
