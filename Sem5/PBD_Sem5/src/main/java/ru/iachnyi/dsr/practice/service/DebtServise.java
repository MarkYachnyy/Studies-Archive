package ru.iachnyi.dsr.practice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.iachnyi.dsr.practice.entity.debt.Debt;
import ru.iachnyi.dsr.practice.repository.DebtRepository;

@Service
public class DebtServise {
    @Autowired
    private DebtRepository debtRepository;

    public boolean payDebt(long userId, long spendingId, int amount){
        Debt existing = debtRepository.findFirstById_userIdAndId_spendingId(userId, spendingId);
        if(existing == null || existing.getAmount() < amount){
            return false;
        } else {
            existing.setAmount(existing.getAmount() - amount);
            debtRepository.save(existing);
            return true;
        }
    }
}
