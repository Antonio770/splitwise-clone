package dev.antonio.splitwise.mappers;

import dev.antonio.splitwise.domain.dto.expenses.SettleUpDto;
import dev.antonio.splitwise.domain.entities.SettleUpEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SettleUpMapper {
    SettleUpEntity toEntity(SettleUpDto settleUpDto);

    @Mapping(source = "fromUser.username", target = "from")
    @Mapping(source = "toUser.username", target = "to")
    SettleUpDto toDto(SettleUpEntity settleUpEntity);
}
