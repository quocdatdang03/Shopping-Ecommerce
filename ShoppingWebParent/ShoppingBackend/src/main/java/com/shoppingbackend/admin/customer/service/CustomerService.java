package com.shoppingbackend.admin.customer.service;

import com.shopping.common.entity.Brand;
import com.shopping.common.entity.Customer;
import com.shoppingbackend.admin.brand.exception.BrandNotFoundException;
import com.shoppingbackend.admin.customer.exception.CustomerNotFoundException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CustomerService {
    public List<Customer> listAllCustomer();
    public Page<Customer> listCustomersByPage(Integer pageNumber ,String sortField , String sortDir, String keyword);
    public Customer saveCustomer(Customer customer);
    public Customer getCustomerById(Integer id) throws CustomerNotFoundException;
    public void deleteCustomerById(Integer id) throws CustomerNotFoundException;
    public void updateEnableStatus(Integer id, boolean enableStatus) throws CustomerNotFoundException;
}
