package ru.iachnyi.dsr.practice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.iachnyi.dsr.practice.response_classes.SimpleSuccessOrErrorResponse;
import ru.iachnyi.dsr.practice.security.SecurityUtils;
import ru.iachnyi.dsr.practice.service.DebtServise;

@RestController
public class DebtController {
    @Autowired
    SecurityUtils securityUtils;

    @Autowired
    private DebtServise debtServise;

    @PatchMapping("/api/debts/pay")
    public SimpleSuccessOrErrorResponse payDebt(@RequestParam("spendingId") long spendingId, @RequestBody int amount){
        SimpleSuccessOrErrorResponse res = new SimpleSuccessOrErrorResponse();
        if(debtServise.payDebt(securityUtils.getCurrentUserId(), spendingId, amount)){
            res.setSuccess("Долг погашен");
        } else {
            res.setSuccess("Произошла ошибка, повторите попытку позже");
        }
        return res;
    }

}
