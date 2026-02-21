package ru.iachnyi.dsr.practice.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import ru.iachnyi.dsr.practice.entity.User;
import ru.iachnyi.dsr.practice.response_classes.SimpleSuccessOrErrorResponse;
import ru.iachnyi.dsr.practice.service.UserService;

@RestController
public class RegisterController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    UserService userRepository;

    @PostMapping("/api/register_user")
    public SimpleSuccessOrErrorResponse registerUser(@RequestBody User user) {
        SimpleSuccessOrErrorResponse response = new SimpleSuccessOrErrorResponse();
        if (userRepository.saveUser(user)) {
            response.setSuccess("Аккаунт успешно зарегистрирован");
        } else {
            response.setError("Аккаунт с таким именем уже существует");
        }
        return response;
    }
}
