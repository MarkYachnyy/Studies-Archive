package ru.iachnyi.dsr.practice.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller

public class FriendsPageController {
    @GetMapping("/friends")
    public String getFriendsPage(Model model) {
        return "friends";
    }
}
