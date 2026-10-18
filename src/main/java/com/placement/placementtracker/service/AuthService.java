package com.placement.placementtracker.service;

import com.placement.placementtracker.dto.auth.AuthResponse;
import com.placement.placementtracker.dto.auth.LoginRequest;
import com.placement.placementtracker.dto.auth.RegisterRequest;
import com.placement.placementtracker.entity.AppUser;
import com.placement.placementtracker.repository.AppUserRepository;
import com.placement.placementtracker.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            AppUserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, "An account with this email already exists"
            );
        }

        AppUser user = new AppUser(
            request.getName().trim(),
            email,
            passwordEncoder.encode(request.getPassword())
        );

        AppUser savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser.getEmail());

        return new AuthResponse(token, savedUser.getName(), savedUser.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        AppUser user = userRepository.findByEmailIgnoreCase(
                request.getEmail().trim())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "Invalid email or password"
            ));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "Invalid email or password"
            );
        }

        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token, user.getName(), user.getEmail());
    }
}
