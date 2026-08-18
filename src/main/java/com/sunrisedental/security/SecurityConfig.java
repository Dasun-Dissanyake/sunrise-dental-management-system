package com.sunrisedental.security;

import com.sunrisedental.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security SecurityFilterChain configuration.
 * Configures authentication providers, session management, and role-based access control.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**")
            )
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/css/**", "/js/**", "/images/**", "/favicon.ico", "/api/v1/health", "/api/v1/auth/login").permitAll()
                // Patient management
                .requestMatchers(HttpMethod.GET, "/patients/**", "/api/v1/patients/**").hasAnyRole("ADMIN", "RECEPTIONIST", "DENTIST")
                .requestMatchers("/patients/**", "/api/v1/patients/**").hasAnyRole("ADMIN", "RECEPTIONIST")
                // Appointment management
                .requestMatchers(HttpMethod.GET, "/appointments/**", "/api/v1/appointments/**").hasAnyRole("ADMIN", "RECEPTIONIST", "DENTIST")
                .requestMatchers("/appointments/**", "/api/v1/appointments/**").hasAnyRole("ADMIN", "RECEPTIONIST")
                // Dentist catalog (read-only for all roles)
                .requestMatchers(HttpMethod.GET, "/api/v1/dentists/**").hasAnyRole("ADMIN", "RECEPTIONIST", "DENTIST")
                .requestMatchers("/api/v1/dentists/**").hasAnyRole("ADMIN", "RECEPTIONIST")
                // Treatment catalog (read-only for all roles)
                .requestMatchers(HttpMethod.GET, "/api/v1/treatments/**").hasAnyRole("ADMIN", "RECEPTIONIST", "DENTIST")
                .requestMatchers("/api/v1/treatments/**").hasAnyRole("ADMIN", "RECEPTIONIST")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            );

        return http.build();
    }
}
