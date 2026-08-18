package com.sunrisedental.service;

import com.sunrisedental.model.Role;
import com.sunrisedental.model.User;
import com.sunrisedental.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Startup component that initializes the admin account if environment variables are provided
 * and no admin user exists.
 */
@Component
public class AdminUserInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        String adminUsername = getEnv("SUNRISE_ADMIN_USERNAME");
        String adminPassword = getEnv("SUNRISE_ADMIN_PASSWORD");

        if (adminUsername == null || adminUsername.isBlank() || adminPassword == null || adminPassword.isBlank()) {
            logger.info("Admin user initialization skipped: SUNRISE_ADMIN_USERNAME or SUNRISE_ADMIN_PASSWORD environment variables not set.");
            return;
        }

        if (userRepository.existsByUsername(adminUsername.trim())) {
            logger.info("Admin user initialization skipped: User with username '{}' already exists.", adminUsername.trim());
            return;
        }

        User adminUser = new User();
        adminUser.setUsername(adminUsername.trim());
        adminUser.setPassword(passwordEncoder.encode(adminPassword));
        adminUser.setFullName("Sunrise Dental Administrator");
        adminUser.setRole(Role.ADMIN);
        adminUser.setEnabled(true);

        userRepository.save(adminUser);
        logger.info("Initial admin user '{}' created successfully.", adminUsername.trim());
    }

    /**
     * Reads environment variable by name. Extracted to allow isolated testing.
     *
     * @param name environment variable key
     * @return environment variable value or null if not set
     */
    protected String getEnv(String name) {
        return System.getenv(name);
    }
}
