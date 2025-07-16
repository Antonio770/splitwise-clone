package dev.antonio.splitwise.domain.dto.expenses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DebtDto {
    private String username;
    private Double amount;
}
