package dev.antonio.splitwise.controllers;

import dev.antonio.splitwise.domain.dto.groups.CreateGroupRequest;
import dev.antonio.splitwise.domain.dto.groups.GroupDto;
import dev.antonio.splitwise.domain.dto.groups.GroupShortDto;
import dev.antonio.splitwise.services.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping(path = "/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @GetMapping
    public ResponseEntity<Set<GroupShortDto>> getAllGroups() {
        Set<GroupShortDto> groups = groupService.getAllGroups();
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupDto> getGroupById(@PathVariable UUID groupId) {
        GroupDto groupDto = groupService.getGroupById(groupId);
        return ResponseEntity.ok(groupDto);
    }

    @PostMapping
    public ResponseEntity<GroupShortDto> createGroup(@Valid @RequestBody CreateGroupRequest createGroupRequest) {
        GroupShortDto groupShortDto = groupService.createGroup(createGroupRequest);
        return new ResponseEntity<>(groupShortDto, HttpStatus.CREATED);
    }

    @PostMapping("/{groupId}/users/{userId}")
    public ResponseEntity<GroupDto> addUserToGroup(
            @PathVariable("groupId") UUID groupId,
            @PathVariable("userId") UUID userId
    ) {
        GroupDto groupDto = groupService.addUserToGroup(groupId, userId);
        return new ResponseEntity<>(groupDto, HttpStatus.CREATED);
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroup(@PathVariable UUID groupId) {
        groupService.deleteGroup(groupId);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{groupId}/users/{userId}")
    public ResponseEntity<Void> deleteUserFromGroup(
            @PathVariable("groupId") UUID groupId,
            @PathVariable("userId") UUID userId
    ) {
        groupService.removeUserFromGroup(groupId, userId);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{groupId}/leave")
    public ResponseEntity<Void> leaveGroup(@PathVariable UUID groupId) {
        groupService.leaveGroup(groupId);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }
}
