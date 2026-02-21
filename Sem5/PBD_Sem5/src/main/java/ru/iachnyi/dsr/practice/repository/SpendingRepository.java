package ru.iachnyi.dsr.practice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.iachnyi.dsr.practice.entity.Spending;

import java.util.List;

public interface SpendingRepository extends JpaRepository<Spending, Long> {

    @Query(value = "SELECT * FROM spendings s WHERE s.id IN (SELECT d.spending_id FROM debts d WHERE d.user_id = ?1)", nativeQuery = true)
    List<Spending> findAllDebtsByUserId(Long id);
}
