package dev.antonio.splitwise.services;

import dev.antonio.splitwise.domain.dto.groups.CreateGroupRequest;
import dev.antonio.splitwise.domain.dto.groups.GroupDto;
import dev.antonio.splitwise.domain.dto.groups.GroupShortDto;
import dev.antonio.splitwise.domain.entities.GroupEntity;

import java.util.Set;
import java.util.UUID;

public interface GroupService {
    Set<GroupShortDto> getAllGroups();
    GroupDto getGroupById(UUID id);
    GroupEntity getGroupEntityById(UUID id);
    GroupShortDto createGroup(CreateGroupRequest createGroupRequest);
    GroupDto addUserToGroup(UUID groupId, UUID userId);
    void deleteGroup(UUID id);
    void removeUserFromGroup(UUID groupId, UUID userId);
    void leaveGroup(UUID id);
}
