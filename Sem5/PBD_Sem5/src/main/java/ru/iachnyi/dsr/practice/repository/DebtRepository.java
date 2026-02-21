package ru.iachnyi.dsr.practice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.iachnyi.dsr.practice.entity.debt.Debt;
import ru.iachnyi.dsr.practice.entity.debt.DebtId;

public interface DebtRepository extends JpaRepository<Debt, DebtId> {
    public Debt findFirstById_userIdAndId_spendingId(Long userId, Long spendingId);
}
