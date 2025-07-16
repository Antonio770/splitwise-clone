package dev.antonio.splitwise.services;

import dev.antonio.splitwise.domain.dto.expenses.BalanceDto;
import dev.antonio.splitwise.domain.dto.expenses.CreateExpenseRequest;
import dev.antonio.splitwise.domain.dto.expenses.ExpenseDto;
import dev.antonio.splitwise.domain.dto.expenses.SettleUpDto;

import java.util.List;
import java.util.UUID;

public interface ExpenseService {
    List<ExpenseDto> getAllExpenses(UUID groupId);
    ExpenseDto getExpenseById(UUID groupId, UUID expenseId);
    ExpenseDto createExpense(UUID groupId, CreateExpenseRequest expenseRequest);
    BalanceDto getBalance(UUID groupId);
    SettleUpDto settleUp(UUID groupId, UUID userId);
}
