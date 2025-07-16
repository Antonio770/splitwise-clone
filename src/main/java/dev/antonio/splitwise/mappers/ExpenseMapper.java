package dev.antonio.splitwise.mappers;

import dev.antonio.splitwise.domain.dto.expenses.ExpenseDto;
import dev.antonio.splitwise.domain.entities.ExpenseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExpenseMapper {
    ExpenseEntity toEntity(ExpenseDto expenseDto);
    ExpenseDto toDto(ExpenseEntity expenseEntity);
}
