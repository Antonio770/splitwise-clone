package dev.antonio.splitwise.domain.dto.expenses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BalanceDto {
    private String user;
    private String group;
    private List<DebtDto> owesTo;
    private List<DebtDto> owedBy;
    private Double netBalance;
}
