package ru.iachnyi.dsr.practice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.iachnyi.dsr.practice.entity.Spending;
import ru.iachnyi.dsr.practice.security.SecurityUtils;
import ru.iachnyi.dsr.practice.service.SpendingService;

import java.util.stream.Collectors;

@Controller
public class SpendingPageController {

    @Autowired
    SpendingService spendingService;

    @Autowired
    SecurityUtils securityUtils;

    @GetMapping("/spending")
    public String getSpendingPage(@RequestParam Long id) {
        Spending spending = spendingService.getSpendingById(id);
        if(spending == null) {
            return "redirect:/spendings";
        } else if(!spending.getDebts().stream().map(debt -> debt.getId().getUserId()).collect(Collectors.toSet()).contains(securityUtils.getCurrentUserId())){
            return "redirect:/spendings";
        }
        return "spending";
    }
}
