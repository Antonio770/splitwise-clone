package dev.antonio.splitwise.controllers;

import dev.antonio.splitwise.domain.dto.users.UserDto;
import dev.antonio.splitwise.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserDto> getUserInformation() {
        return ResponseEntity.ok(userService.getUserInformation());
    }

}
