package com.shopping.web.customer.controller;

import com.shopping.web.customer.service.CustomerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerRestController {

    @Autowired
    private CustomerServiceImpl customerService;

    @PostMapping("/customers/check_unique_email")
    public String checkEmail(@Param("email") String email) {
        return customerService.isUniqueEmail(email) ? "Ok" : "Duplicated";
    }
}
