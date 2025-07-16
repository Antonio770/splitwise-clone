package dev.antonio.splitwise.controllers;

import dev.antonio.splitwise.domain.dto.authentication.AuthResponse;
import dev.antonio.splitwise.domain.dto.authentication.LoginRequest;
import dev.antonio.splitwise.domain.dto.authentication.RegisterRequest;
import dev.antonio.splitwise.domain.dto.users.UserShortDto;
import dev.antonio.splitwise.domain.entities.UserEntity;
import dev.antonio.splitwise.mappers.UserMapper;
import dev.antonio.splitwise.repositories.UserRepository;
import dev.antonio.splitwise.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @PostMapping(path = "/register")
    public ResponseEntity<UserShortDto> register(@RequestBody RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        UserEntity userEntity = UserEntity.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .build();

        UserEntity savedUserEntity = userRepository.save(userEntity);
        UserShortDto userShortDto = userMapper.toShortDto(savedUserEntity);
        return new ResponseEntity<>(userShortDto, HttpStatus.CREATED);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        UserDetails userDetails = authenticationService.authenticate(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        );

        String token = authenticationService.generateToken(userDetails);

        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .expiresIn(86400)
                .build();

        return ResponseEntity.ok(authResponse);
    }
}
