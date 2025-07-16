package dev.antonio.splitwise.controllers;

import dev.antonio.splitwise.domain.dto.expenses.BalanceDto;
import dev.antonio.splitwise.domain.dto.expenses.CreateExpenseRequest;
import dev.antonio.splitwise.domain.dto.expenses.ExpenseDto;
import dev.antonio.splitwise.domain.dto.expenses.SettleUpDto;
import dev.antonio.splitwise.services.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/groups/{groupId}")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping("/expenses")
    public ResponseEntity<List<ExpenseDto>> getAllExpenses(@PathVariable UUID groupId) {
        List<ExpenseDto> expenses = expenseService.getAllExpenses(groupId);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/expenses/{expenseId}")
    public ResponseEntity<ExpenseDto> getExpenseById(@PathVariable UUID groupId, @PathVariable UUID expenseId) {
        return ResponseEntity.ok(expenseService.getExpenseById(groupId, expenseId));
    }

    @PostMapping("/expenses")
    public ResponseEntity<ExpenseDto> createExpense(
            @PathVariable UUID groupId,
            @Valid @RequestBody CreateExpenseRequest expenseRequestDto
    ) {
        ExpenseDto expenseDto = expenseService.createExpense(groupId, expenseRequestDto);
        return new ResponseEntity<>(expenseDto, HttpStatus.CREATED);
    }

    @GetMapping("/balance")
    public ResponseEntity<BalanceDto> getBalance(@PathVariable UUID groupId) {
        return ResponseEntity.ok(expenseService.getBalance(groupId));
    }

    @PostMapping("/settle-up/{userId}")
    public ResponseEntity<SettleUpDto> settleUp(
            @PathVariable UUID groupId,
            @PathVariable UUID userId
    ) {
        SettleUpDto settleUpDto = expenseService.settleUp(groupId, userId);
        return ResponseEntity.ok(settleUpDto);
    }
}
