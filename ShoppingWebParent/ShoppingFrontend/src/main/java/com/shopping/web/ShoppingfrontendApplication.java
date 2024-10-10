package com.shopping.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
// Vì ta đang ở module của ShoppingBackend -> để Spring Data JPA ở module này manage @Entity ở module ShoppingCommon thì dùng @EntityScan
@EntityScan(basePackages = {"com.shopping.common.entity"})
public class ShoppingfrontendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingfrontendApplication.class, args);
    }

}
