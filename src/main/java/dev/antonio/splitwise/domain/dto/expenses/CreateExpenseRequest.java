package dev.antonio.splitwise.domain.dto.expenses;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateExpenseRequest {

    @Size(max = 50, message = "Description should have a maximum of {max} characters")
    private String description;

    @NotNull(message = "Amount must be provided")
    @Positive(message = "Amount must be a positive number")
    private Double amount;

}
