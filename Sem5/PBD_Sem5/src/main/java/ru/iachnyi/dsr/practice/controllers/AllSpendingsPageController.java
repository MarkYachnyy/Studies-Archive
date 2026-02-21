package ru.iachnyi.dsr.practice.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AllSpendingsPageController {
    @GetMapping("/spendings")
    public String getAllSpendingsPage(Model model) {
        return "spendings";
    }
}
