package dev.antonio.splitwise.domain.dto.expenses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SettleUpDto {
    private Double amount;
    private String from;
    private String to;
}
