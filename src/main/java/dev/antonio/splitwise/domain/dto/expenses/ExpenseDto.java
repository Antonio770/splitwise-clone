package dev.antonio.splitwise.domain.dto.expenses;

import dev.antonio.splitwise.domain.dto.users.UserShortDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenseDto {
    private UUID id;
    private String description;
    private Double amount;
    private UserShortDto paidBy;
}
