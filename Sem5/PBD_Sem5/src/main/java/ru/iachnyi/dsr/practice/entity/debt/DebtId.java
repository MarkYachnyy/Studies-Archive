package ru.iachnyi.dsr.practice.entity.debt;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.iachnyi.dsr.practice.entity.Spending;

import java.io.Serializable;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class DebtId implements Serializable {
    @JoinColumn(name="id")
    private Long spendingId;
    private Long userId;
}
