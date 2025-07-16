package dev.antonio.splitwise.domain.dto.groups;

import dev.antonio.splitwise.domain.dto.users.UserShortDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupDto {
    private UUID id;
    private String name;
    private UserShortDto creator;
    private Set<UserShortDto> users;
}