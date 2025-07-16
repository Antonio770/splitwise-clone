package dev.antonio.splitwise.mappers;

import dev.antonio.splitwise.domain.dto.groups.GroupDto;
import dev.antonio.splitwise.domain.dto.groups.GroupShortDto;
import dev.antonio.splitwise.domain.entities.GroupEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GroupMapper {
    GroupEntity toEntity(GroupDto groupDto);
    GroupDto toDto(GroupEntity groupEntity);
    GroupShortDto toShortDto(GroupEntity groupEntity);
}
