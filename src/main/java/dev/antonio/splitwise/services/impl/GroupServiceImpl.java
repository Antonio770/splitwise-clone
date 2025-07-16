package dev.antonio.splitwise.services.impl;

import dev.antonio.splitwise.domain.dto.groups.CreateGroupRequest;
import dev.antonio.splitwise.domain.dto.groups.GroupDto;
import dev.antonio.splitwise.domain.dto.groups.GroupShortDto;
import dev.antonio.splitwise.domain.entities.GroupEntity;
import dev.antonio.splitwise.domain.entities.UserEntity;
import dev.antonio.splitwise.mappers.GroupMapper;
import dev.antonio.splitwise.mappers.UserMapper;
import dev.antonio.splitwise.repositories.GroupRepository;
import dev.antonio.splitwise.repositories.UserRepository;
import dev.antonio.splitwise.services.AuthenticationService;
import dev.antonio.splitwise.services.GroupService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final AuthenticationService authenticationService;
    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public Set<GroupShortDto> getAllGroups() {
        UserEntity userEntity = authenticationService.getUserEntityFromAuth();

        return userEntity.getGroups().stream()
                .map(groupMapper::toShortDto)
                .collect(Collectors.toSet());
    }

    @Override
    public GroupDto getGroupById(UUID id) {
        GroupEntity groupEntity = groupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Group ID not found"));

        return groupMapper.toDto(groupEntity);
    }

    @Override
    public GroupEntity getGroupEntityById(UUID id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Group ID not found"));
    }

    @Override
    @Transactional
    public GroupShortDto createGroup(CreateGroupRequest createGroupRequest) {
        if (groupRepository.existsByName(createGroupRequest.getName())) {
            throw new IllegalArgumentException("Group name already exists");
        }

        UserEntity creatorUserEntity = authenticationService.getUserEntityFromAuth();

        GroupEntity groupEntity = GroupEntity.builder()
                .name(createGroupRequest.getName())
                .creator(creatorUserEntity)
                .build();

        groupEntity.getUsers().add(creatorUserEntity);
        GroupEntity savedGroupEntity = groupRepository.save(groupEntity);
        creatorUserEntity.getGroups().add(groupEntity);

        userRepository.save(creatorUserEntity);
        return groupMapper.toShortDto(savedGroupEntity);
    }

    @Override
    @Transactional
    public GroupDto addUserToGroup(UUID groupId, UUID userId) {
        GroupEntity groupEntity = getGroupEntityById(groupId);

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User ID not found"));

        if (groupEntity.getUsers().contains(userEntity)) {
            throw new IllegalArgumentException("User is already part of the group");
        }

        if (userEntity.getGroups() == null) {
            userEntity.setGroups(new HashSet<>());
        }

        groupEntity.getUsers().add(userEntity);
        userEntity.getGroups().add(groupEntity);

        userRepository.save(userEntity);
        return groupMapper.toDto(groupRepository.save(groupEntity));
    }

    @Override
    public void deleteGroup(UUID id) {
        GroupEntity groupEntity = getGroupEntityById(id);
        UserEntity userEntity = authenticationService.getUserEntityFromAuth();

        if (!groupEntity.getCreator().getId().equals(userEntity.getId())) {
            throw new AccessDeniedException("Only the creator of the group can delete it");
        }

        for (UserEntity user : groupEntity.getUsers()) {
            user.getGroups().remove(groupEntity);
        }

        groupRepository.delete(groupEntity);
    }

    @Override
    @Transactional
    public void removeUserFromGroup(UUID groupId, UUID userId) {
        GroupEntity groupEntity = getGroupEntityById(groupId);
        UserEntity authUserEntity = authenticationService.getUserEntityFromAuth();

        UserEntity deletedUserEntity = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User ID not found"));

        if (!groupEntity.getUsers().contains(deletedUserEntity)) {
            throw new IllegalArgumentException("User is not part of the group");
        }

        if (!groupEntity.getCreator().getId().equals(authUserEntity.getId())) {
            throw new AccessDeniedException("Only the group creator can remove users");
        }

        if (authUserEntity.getId().equals(deletedUserEntity.getId())) {
            leaveGroup(groupId);
        }

        deletedUserEntity.getGroups().remove(groupEntity);
        groupEntity.getUsers().remove(deletedUserEntity);

        userRepository.save(deletedUserEntity);
        groupRepository.save(groupEntity);
    }

    @Override
    @Transactional
    public void leaveGroup(UUID id) {
        GroupEntity groupEntity = getGroupEntityById(id);
        UserEntity userEntity = authenticationService.getUserEntityFromAuth();

        if (groupEntity.getCreator().getId().equals(userEntity.getId()) && groupEntity.getUsers().size() > 1) {
            throw new AccessDeniedException("Group creator can't leave while other users are part of the group");
        }

        if (userEntity.getGroups() == null || !userEntity.getGroups().contains(groupEntity)) {
            throw new IllegalArgumentException("User is not part of the group");
        }

        if (groupEntity.getUsers().size() == 1) {
            groupRepository.delete(groupEntity);
            return;
        }

        userEntity.getGroups().remove(groupEntity);
        groupEntity.getUsers().remove(userEntity);

        userRepository.save(userEntity);
        groupRepository.save(groupEntity);
    }
}
