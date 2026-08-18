package com.sunrisedental.service;

import com.sunrisedental.dto.UserResponse;
import com.sunrisedental.exception.CustomApiException;
import com.sunrisedental.model.User;
import com.sunrisedental.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementation of UserService for handling user domain operations.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findUserEntityByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(UserResponse::fromUser)
                .orElseThrow(() -> new CustomApiException("User not found with username: " + username));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public UserResponse createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new CustomApiException("Username is already taken");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return UserResponse.fromUser(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new CustomApiException("No authenticated user session found");
        }
        String username = authentication.getName();
        return findByUsername(username);
    }
}
