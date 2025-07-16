package dev.antonio.splitwise.mappers;

import dev.antonio.splitwise.domain.dto.users.UserDto;
import dev.antonio.splitwise.domain.dto.users.UserShortDto;
import dev.antonio.splitwise.domain.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserEntity toEntity(UserDto userDto);
    UserDto toDto(UserEntity userEntity);
    UserShortDto toShortDto(UserEntity userEntity);
}
