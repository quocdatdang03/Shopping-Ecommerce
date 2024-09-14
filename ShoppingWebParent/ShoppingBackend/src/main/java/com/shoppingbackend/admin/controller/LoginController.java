package com.shoppingbackend.admin.controller;

import com.shoppingbackend.admin.security.ShoppingUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String showLoginForm()
    {
        return "login";
    }
}
