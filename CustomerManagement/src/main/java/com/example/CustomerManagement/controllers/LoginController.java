package com.example.CustomerManagement.controllers;

import com.example.CustomerManagement.entities.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(HttpSession session, @RequestParam String name, @RequestParam String password, Model model) {
        if ("test@sunbasedata.com".equals(name) && "Test@123".equals(password)) {
            session.setAttribute("user", new User("test@sunbasedata.com", "Test@123"));
            model.addAttribute("authenticatedUser", name);
            return "redirect:/customers";
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logOut(HttpSession session){
        session.invalidate();
        return "redirect:/login";
    }
}
