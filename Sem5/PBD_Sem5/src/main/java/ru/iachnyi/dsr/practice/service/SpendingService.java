package ru.iachnyi.dsr.practice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.iachnyi.dsr.practice.entity.Spending;
import ru.iachnyi.dsr.practice.entity.debt.Debt;
import ru.iachnyi.dsr.practice.repository.SpendingRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SpendingService {
    @Autowired
    private SpendingRepository spendingRepository;

    public void saveSpending(Spending spending) {
        Set<Debt> debts = spending.getDebts();
        spending.setDebts(new HashSet<>());
        Spending written = spendingRepository.save(spending);
        debts.forEach(debt -> debt.getId().setSpendingId(written.getId()));
        spending.setDebts(debts);
        spendingRepository.save(spending);
    }

    public void deleteSpending(Spending spending) {
        spendingRepository.delete(spending);
    }

    public List<Spending> getAllSpendingsByUserId(Long userId) {
        return spendingRepository.findAllDebtsByUserId(userId);
    }

    public Spending getSpendingById(Long id) {
        return spendingRepository.findById(id).orElse(null);
    }
}
