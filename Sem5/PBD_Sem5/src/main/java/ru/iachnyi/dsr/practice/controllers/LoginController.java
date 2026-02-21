package ru.iachnyi.dsr.practice.controllers;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
	
	@GetMapping("/login")
	public String getLoginPage(Model model) {
		if(SecurityContextHolder.getContext().getAuthentication() != null) {
			return "redirect:/spendings";
		}
		return "login";
	}
	
}
