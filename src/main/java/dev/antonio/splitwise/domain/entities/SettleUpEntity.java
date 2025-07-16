package dev.antonio.splitwise.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "settleUps")
public class SettleUpEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Double amount;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity fromUser;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity toUser;

    @ManyToOne(fetch = FetchType.LAZY)
    private GroupEntity group;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SettleUpEntity that = (SettleUpEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
