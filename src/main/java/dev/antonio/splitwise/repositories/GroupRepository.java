package dev.antonio.splitwise.repositories;

import dev.antonio.splitwise.domain.entities.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<GroupEntity, UUID> {
    boolean existsByName(String name);
}
