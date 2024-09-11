package com.shoppingbackend.admin.user.controller;

import com.shoppingbackend.admin.user.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// Sử dụng Rest API : để return về Http Servlet Response cho web client :
@RestController
public class UserRestController {

    @Autowired
    private UserServiceImpl userService;

    @PostMapping("/users/check_email")
    public String checkDuplicateEmail(@RequestParam(value = "id", required = false) Integer id,@RequestParam("email") String email)
    {
        boolean isEmailUnique = userService.isEmailUnique(id, email);
        return isEmailUnique ? "ok" : "duplicate";
    }
}
