package com.sunrisedental.repository;

import com.sunrisedental.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository for User persistence operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by unique username.
     *
     * @param username the username to query
     * @return Optional containing the User if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Check whether a user with the given username exists.
     *
     * @param username the username to check
     * @return true if exists, false otherwise
     */
    boolean existsByUsername(String username);
}
