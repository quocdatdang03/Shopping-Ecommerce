package com.shopping.web.customer.service;

import com.shopping.common.entity.Country;
import com.shopping.common.entity.Customer;

import java.util.List;

public interface CustomerService {
    public List<Country> findAllCountry();
    public boolean isUniqueEmail(String email);
    public void registerCustomer(Customer customer);
    public boolean verifyCustomerAccount(String verificationCode);
}
