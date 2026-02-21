package ru.iachnyi.dsr.practice.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.iachnyi.dsr.practice.entity.Spending;
import ru.iachnyi.dsr.practice.entity.debt.Debt;
import ru.iachnyi.dsr.practice.repository.UserRepository;
import ru.iachnyi.dsr.practice.response_classes.Page;
import ru.iachnyi.dsr.practice.response_classes.SimpleSuccessOrErrorResponse;
import ru.iachnyi.dsr.practice.response_classes.SpendingResponse;
import ru.iachnyi.dsr.practice.security.SecurityUtils;
import ru.iachnyi.dsr.practice.service.SpendingService;


import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class SpendingsController {

    private static final Logger log = LoggerFactory.getLogger(SpendingsController.class);
    @Autowired
    SecurityUtils securityUtils;

    @Autowired
    SpendingService spendingService;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/api/spendings/all")
    public List<SpendingResponse> getAllSpendings() {
        return spendingService.getAllSpendingsByUserId(securityUtils.getCurrentUserId()).
                stream().map(this::boxSpending).collect(Collectors.toList());
    }

    @GetMapping("/api/spendings/part/{from}-{to}-{sortby}")
    public Page<SpendingResponse> getSomeSpendings(@PathVariable int from, @PathVariable int to, @PathVariable String sortby){
        List<SpendingResponse> all = getAllSpendings();
        String currName = securityUtils.getCurrentUserName();
        Comparator<SpendingResponse> comparator = Comparator.comparing(SpendingResponse::getName);
        if(sortby.equals("dateasc")){
            comparator = Comparator.comparing(SpendingResponse::getDate);
        } else if (sortby.equals("datedesc")){
            comparator = (s1, s2) -> s2.getDate().compareTo(s1.getDate());
        } else if(sortby.equals("debtasc")){
            comparator = Comparator.comparingInt(s -> s.getDebts().get(currName));
        } else if(sortby.equals("debtdesc")){
            comparator = (s1, s2) -> Integer.compare(s2.getDebts().get(currName), s1.getDebts().get(currName));
        }
        all.sort(comparator);
        return new Page<>(all.size(), all.subList(from, Math.min(all.size(), to)));
    }

    @PostMapping("/api/spendings/new")
    public SimpleSuccessOrErrorResponse createSpending(@RequestBody SpendingResponse spending) {
        SimpleSuccessOrErrorResponse res = new SimpleSuccessOrErrorResponse();
        Spending toSave = unboxSpending(spending);
        toSave.setId(null);
        spendingService.saveSpending(toSave);
        res.setSuccess("Счёт создан!");
        return res;
    }

    @GetMapping("/api/spendings/{id}")
    public SpendingResponse getSpendingById(@PathVariable Long id) {
        return boxSpending(spendingService.getSpendingById(id));
    }

    @DeleteMapping("/api/spendings/delete/{id}")
    private SimpleSuccessOrErrorResponse deleteSpending(@PathVariable Long id) {
        SimpleSuccessOrErrorResponse res = new SimpleSuccessOrErrorResponse();
        try {
            Spending toDelete = spendingService.getSpendingById(id);
            spendingService.deleteSpending(toDelete);
            res.setSuccess("Удалено");
        } catch (Exception e) {
            res.setError("Ошибка");
        }
        return res;
    }

    @PatchMapping("/api/spendings/rename/{id}")
    private SimpleSuccessOrErrorResponse renameSpending(@PathVariable Long id, @RequestBody String newName) {
        SimpleSuccessOrErrorResponse res = new SimpleSuccessOrErrorResponse();
        try{
            Spending existing = spendingService.getSpendingById(id);
            if(existing == null){
                res.setError("Unknown error");
                return res;
            }
            existing.setName(newName);
            spendingService.saveSpending(existing);
        } catch (Exception e){
            res.setError("Error occurred");
        }
        res.setSuccess("Rename successful");
        return res;
    }

    private SpendingResponse boxSpending(Spending spending) {
        SpendingResponse spendingResponse = new SpendingResponse();
        spendingResponse.setId(spending.getId());
        spendingResponse.setCreatorName(userRepository.findById(spending.getCreatorId()).get().getName());
        spendingResponse.setPayerName(userRepository.findById(spending.getPayerId()).get().getName());
        spendingResponse.setDate(spending.getDate().toString());
        spendingResponse.setName(spending.getName());
        Map<String, Integer> debts = new HashMap<>();
        spending.getDebts().forEach(debt -> debts.put(userRepository.findById(debt.getId().getUserId()).get().getName(), debt.getAmount()));
        spendingResponse.setDebts(debts);
        return spendingResponse;
    }

    private Spending unboxSpending(SpendingResponse spendingResponse) {
        Spending spending = new Spending();
        spending.setId(spendingResponse.getId());
        spending.setDate(Date.valueOf(spendingResponse.getDate()));
        spending.setName(spendingResponse.getName());
        spending.setPayerId(userRepository.findByName(spendingResponse.getPayerName()).get().getId());
        spending.setCreatorId(userRepository.findByName(spendingResponse.getCreatorName()).get().getId());
        Set<Debt> debts = new HashSet<>();
        for(Map.Entry<String, Integer> debt : spendingResponse.getDebts().entrySet()) {
            debts.add(new Debt(userRepository.findByName(debt.getKey()).get().getId(), debt.getValue()));
        }
        spending.setDebts(debts);
        return spending;
    }
}
