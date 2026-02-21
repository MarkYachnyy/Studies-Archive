package ru.iachnyi.dsr.practice.entity.debt;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.iachnyi.dsr.practice.entity.Spending;

@Entity
@Table(name = "debts")
@Data
@NoArgsConstructor
public class Debt {

    @EmbeddedId
    private DebtId id;
    private Integer amount;

    public Debt(Long userId, Integer amount) {
        this.setId(new DebtId(null, userId));
        this.setAmount(amount);
    }
}
