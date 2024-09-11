package com.shoppingbackend.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {
    @GetMapping("/")
    public String showAdminPage()
    {
        return "index";
    }
}
