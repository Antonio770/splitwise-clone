package dev.antonio.splitwise.services.impl;

import dev.antonio.splitwise.domain.dto.users.UserDto;
import dev.antonio.splitwise.domain.entities.UserEntity;
import dev.antonio.splitwise.mappers.UserMapper;
import dev.antonio.splitwise.services.AuthenticationService;
import dev.antonio.splitwise.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AuthenticationService authenticationService;
    private final UserMapper userMapper;

    @Override
    public UserDto getUserInformation() {
        UserEntity userEntity = authenticationService.getUserEntityFromAuth();
        return userMapper.toDto(userEntity);
    }
}
