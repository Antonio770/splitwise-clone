package dev.antonio.splitwise.repositories;

import dev.antonio.splitwise.domain.entities.SettleUpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SettleUpRepository extends JpaRepository<SettleUpEntity, UUID> {
}
