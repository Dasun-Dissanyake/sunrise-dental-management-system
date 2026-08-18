package com.sunrisedental.service;

import com.sunrisedental.dto.UserResponse;
import com.sunrisedental.model.User;

import java.util.Optional;

/**
 * Service interface for user operations.
 */
public interface UserService {

    Optional<User> findUserEntityByUsername(String username);

    UserResponse findByUsername(String username);

    boolean existsByUsername(String username);

    UserResponse createUser(User user);

    UserResponse getCurrentAuthenticatedUser();
}
