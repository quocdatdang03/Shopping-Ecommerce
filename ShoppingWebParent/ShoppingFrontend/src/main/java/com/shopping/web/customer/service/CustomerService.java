package com.shopping.web.customer.service;

import com.shopping.common.entity.AuthenticationType;
import com.shopping.common.entity.Country;
import com.shopping.common.entity.Customer;
import com.shopping.common.exception.CustomerNotFoundException;

import java.util.List;

public interface CustomerService {
    public List<Country> findAllCountry();
    public Customer getCustomerByEmail(String email);
    public Customer getCustomerByResetPasswordToken(String resetPasswordToken);
    public boolean isUniqueEmail(String email);
    public void registerCustomer(Customer customer);
    public boolean verifyCustomerAccount(String verificationCode);
    public void resetPassword(String newPassword, String resetPasswordToken) throws CustomerNotFoundException;
    public void updateAuthenticationType(Customer customer, AuthenticationType authenticationType);
    public void addNewCustomerUponOAuthLogin(String name, String email, String countryCode);
    public String updateResetPasswordToken(String email) throws CustomerNotFoundException;
}
