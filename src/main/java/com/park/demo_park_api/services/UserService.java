package com.park.demo_park_api.services;

import com.park.demo_park_api.entities.User;
import com.park.demo_park_api.exception.EntityNotFoundException;
import com.park.demo_park_api.exception.PasswordInvalidException;
import com.park.demo_park_api.exception.UsernameUniqueViolationException;
import com.park.demo_park_api.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User insert(User user) {
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new UsernameUniqueViolationException(String.format("Username {%s} already registered", user.getUsername()));
        }
    }

    @Transactional
    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("User id=%s not found", id))
        );
    }

    @Transactional
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException(String.format("Username =%s not found", username))
        );
    }

    @Transactional
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public User updatePassword(Long id, String currentPassword, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new PasswordInvalidException("Passwords do not match");
        }

        User user = findById(id);
        if (!user.getPassword().equals(currentPassword)) {
            throw new PasswordInvalidException("Wrong current password");
        }

        user.setPassword(newPassword);
        return user;
    }
}
