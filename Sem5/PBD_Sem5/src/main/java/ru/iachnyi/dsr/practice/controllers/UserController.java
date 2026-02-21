package ru.iachnyi.dsr.practice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.iachnyi.dsr.practice.entity.User;
import ru.iachnyi.dsr.practice.repository.UserRepository;
import ru.iachnyi.dsr.practice.security.SecurityUtils;

@RestController
public class UserController {

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/api/user/current")
    public User getUser(){
        return userRepository.findByName(securityUtils.getCurrentUserName()).orElse(null);
    }

    @GetMapping("/api/user/exists")
    public boolean existsUser(@RequestParam String name){
        User user = userRepository.findByName(name).orElse(null);
        return user != null;
    }
}
