package dev.antonio.splitwise.services.impl;

import dev.antonio.splitwise.domain.dto.expenses.*;
import dev.antonio.splitwise.domain.entities.ExpenseEntity;
import dev.antonio.splitwise.domain.entities.GroupEntity;
import dev.antonio.splitwise.domain.entities.SettleUpEntity;
import dev.antonio.splitwise.domain.entities.UserEntity;
import dev.antonio.splitwise.mappers.ExpenseMapper;
import dev.antonio.splitwise.mappers.SettleUpMapper;
import dev.antonio.splitwise.repositories.ExpenseRepository;
import dev.antonio.splitwise.repositories.GroupRepository;
import dev.antonio.splitwise.repositories.SettleUpRepository;
import dev.antonio.splitwise.repositories.UserRepository;
import dev.antonio.splitwise.services.AuthenticationService;
import dev.antonio.splitwise.services.ExpenseService;
import dev.antonio.splitwise.services.GroupService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final AuthenticationService authenticationService;
    private final GroupService groupService;

    private final ExpenseRepository expenseRepository;
    private final SettleUpRepository settleUpRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    private final ExpenseMapper expenseMapper;
    private final SettleUpMapper settleUpMapper;

    @Override
    public List<ExpenseDto> getAllExpenses(UUID groupId) {
        GroupEntity groupEntity = groupService.getGroupEntityById(groupId);

        return groupEntity.getExpenses().stream()
                .map(expenseMapper::toDto)
                .toList();
    }

    @Override
    public ExpenseDto getExpenseById(UUID groupId, UUID expenseId) {
        GroupEntity groupEntity = groupService.getGroupEntityById(groupId);
        ExpenseEntity expenseEntity = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new EntityNotFoundException("Expense ID not found"));

        if (!groupEntity.getExpenses().contains(expenseEntity)) {
            throw new EntityNotFoundException("Expense not found in group with id " + groupId);
        }

        return expenseMapper.toDto(expenseEntity);
    }

    @Override
    @Transactional
    public ExpenseDto createExpense(UUID groupId, CreateExpenseRequest expenseRequest) {
        GroupEntity groupEntity = groupService.getGroupEntityById(groupId);
        UserEntity userEntity = authenticationService.getUserEntityFromAuth();

        if (!groupEntity.getUsers().contains(userEntity)) {
            throw new AccessDeniedException("User is not part of the group");
        }

        ExpenseEntity expenseEntity = ExpenseEntity.builder()
                .amount(expenseRequest.getAmount())
                .description(expenseRequest.getDescription())
                .paidBy(userEntity)
                .group(groupEntity)
                .build();

        groupEntity.getExpenses().add(expenseEntity);
        groupRepository.save(groupEntity);
        return expenseMapper.toDto(expenseRepository.save(expenseEntity));
    }

    @Override
    public BalanceDto getBalance(UUID groupId) {
        GroupEntity groupEntity = groupService.getGroupEntityById(groupId);
        UserEntity authUserEntity = authenticationService.getUserEntityFromAuth();

        if (!groupEntity.getUsers().contains(authUserEntity)) {
            throw new AccessDeniedException("User is not part of the group");
        }

        Map<String, Double> debtMap = getDebtMap(groupEntity, authUserEntity);

        Double netBalance = debtMap.values().stream()
                .map(value -> -value)
                .reduce(0.0, Double::sum);

        List<DebtDto> owesTo = debtMap.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .map(this::convertMapEntryToDebtDto)
                .toList();

        List<DebtDto> owedBy = debtMap.entrySet().stream()
                .filter(entry -> entry.getValue() < 0)
                .map(this::convertMapEntryToDebtDto)
                .toList();

        return BalanceDto.builder()
                .user(authUserEntity.getUsername())
                .group(groupEntity.getName())
                .owesTo(owesTo)
                .owedBy(owedBy)
                .netBalance(Math.round(netBalance * 100.0) / 100.0)
                .build();
    }

    @Override
    public SettleUpDto settleUp(UUID groupId, UUID userId) {
        GroupEntity groupEntity = groupService.getGroupEntityById(groupId);
        UserEntity targetUserEntity = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User ID not found"));
        UserEntity authUserEntity = authenticationService.getUserEntityFromAuth();

        if (!groupEntity.getUsers().contains(targetUserEntity) || !groupEntity.getUsers().contains(authUserEntity)) {
            throw new AccessDeniedException("User is not part of the group");
        }

        BalanceDto fromUserBalanceDto = getBalance(groupId);
        Double amount = getSettleUpAmount(fromUserBalanceDto, targetUserEntity);

        if (amount == 0) {
            throw new IllegalArgumentException(
                    "There is no debt to be settled up with user " + targetUserEntity.getUsername()
            );
        }

        boolean authUserOwes = fromUserBalanceDto.getOwesTo().stream()
                .anyMatch(debtDto -> debtDto.getUsername().equals(targetUserEntity.getUsername()));

        SettleUpEntity settleUpEntity = SettleUpEntity.builder()
                    .amount(amount)
                    .toUser(authUserOwes ? targetUserEntity : authUserEntity)
                    .fromUser(authUserOwes ? authUserEntity : targetUserEntity)
                    .group(groupEntity)
                    .build();


        groupEntity.getSettleUps().add(settleUpEntity);
        groupRepository.save(groupEntity);

        return settleUpMapper.toDto(settleUpEntity);
    }

    /**
     * Gets the amount of money a user owes/is owed by another user
     * @param fromUserBalanceDto The balance report of the authenticated user
     * @param toUserEntity The target user
     * @return The amount of money owed
     */
    private Double getSettleUpAmount(BalanceDto fromUserBalanceDto, UserEntity toUserEntity) {
        Optional<DebtDto> owesTo = fromUserBalanceDto.getOwesTo().stream()
                .filter(debtDto -> debtDto.getUsername().equals(toUserEntity.getUsername()))
                .findAny();

        Optional<DebtDto> owedBy = fromUserBalanceDto.getOwedBy().stream()
                .filter(debtDto -> debtDto.getUsername().equals(toUserEntity.getUsername()))
                .findAny();

        if (owesTo.isPresent()) {
            return owesTo.get().getAmount();
        } else if (owedBy.isPresent()) {
            return owedBy.get().getAmount();
        }

        return (double) 0;
    }

    /**
     * Creates a HashMap that keeps track of the amount of money the authenticated user owes/is owed
     * @param groupEntity The group for which the balance is getting calculated
     * @param authUserEntity The current authenticated user
     * @return A HashMap containing the amount of money owed to each user in the group
     */
    private Map<String, Double> getDebtMap(GroupEntity groupEntity, UserEntity authUserEntity) {
        Map<String, Double> debtMap = new HashMap<>();
        Integer groupSize = groupEntity.getUsers().size();

        // Add all users to the hashmap, except for the authenticated user
        for (UserEntity user : groupEntity.getUsers()) {
            if (!user.getId().equals(authUserEntity.getId())) {
                debtMap.put(user.getUsername(), 0.0);
            }
        }

        // Go through each expense and check if user owes or is owed money
        for (ExpenseEntity expenseEntity : groupEntity.getExpenses()) {
            Double amountPerUser = expenseEntity.getAmount() / groupSize;
            UserEntity paidByUser = expenseEntity.getPaidBy();

            // Someone else paid, authenticated user owes them money
            if (!paidByUser.getId().equals(authUserEntity.getId())) {
                debtMap.put(paidByUser.getUsername(), debtMap.get(paidByUser.getUsername()) + amountPerUser);
                continue;
            }

            // Authenticated user paid, other users owe him money
            for (UserEntity owedUserEntity : groupEntity.getUsers()) {
                if (owedUserEntity.getId().equals(authUserEntity.getId())) {
                    continue;
                }

                String username = owedUserEntity.getUsername();
                debtMap.put(username, debtMap.get(username) - amountPerUser);
            }
        }

        // Only take the settle-ups the authenticated user is involved in
        List<SettleUpEntity> involvedSettleUps = groupEntity.getSettleUps().stream()
                .filter(settleUp ->
                        settleUp.getFromUser().getId().equals(authUserEntity.getId())
                     || settleUp.getToUser().getId().equals(authUserEntity.getId()))
                .toList();

        // Go through each settle-up and increase/decrease the debt to other users
        for (SettleUpEntity settleUp : involvedSettleUps) {
            String fromUser = settleUp.getFromUser().getUsername();
            String toUser = settleUp.getToUser().getUsername();
            Double amount = settleUp.getAmount();

            if (authUserEntity.getUsername().equals(fromUser)) {
                debtMap.put(toUser, debtMap.get(toUser) - amount);
            } else {
                debtMap.put(fromUser, debtMap.get(fromUser) + amount);
            }
        }

        return debtMap;
    }

    /**
     * Converts a debt HashMap entry to a DebtDto
     * @param entry The HashMap entry
     * @return A DebtDto containing the username and amount owed
     */
    private DebtDto convertMapEntryToDebtDto(Map.Entry<String, Double> entry) {
        return DebtDto.builder()
                .username(entry.getKey())
                .amount(Math.abs(Math.round(entry.getValue() * 100.0) / 100.0))
                .build();
    }
}
