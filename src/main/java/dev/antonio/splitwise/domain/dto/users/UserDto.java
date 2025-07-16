package dev.antonio.splitwise.domain.dto.users;

import dev.antonio.splitwise.domain.dto.groups.GroupShortDto;
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
public class UserDto {
        private UUID id;
        private String username;
        private Set<GroupShortDto> groups;
}
