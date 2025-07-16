package dev.antonio.splitwise.services;

import dev.antonio.splitwise.domain.entities.UserEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

public interface AuthenticationService {
    UserDetails authenticate(String username, String password);
    String generateToken(UserDetails userDetails);
    UserDetails validateToken(String token);
    UserEntity getUserEntityFromAuth();
}
