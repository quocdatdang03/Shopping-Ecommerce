package com.shoppingbackend.admin.customer.service;

import com.shopping.common.entity.Brand;
import com.shopping.common.entity.Customer;
import com.shoppingbackend.admin.brand.exception.BrandNotFoundException;
import com.shoppingbackend.admin.customer.exception.CustomerNotFoundException;
import com.shoppingbackend.admin.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CustomerServiceImpl implements CustomerService {

    public static final int CUSTOMER_NUMBER_PER_PAGE = 5;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<Customer> listAllCustomer() {
        return (List<Customer>) customerRepository.findAll();
    }

    @Override
    public Page<Customer> listCustomersByPage(Integer pageNumber, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNumber-1, CUSTOMER_NUMBER_PER_PAGE, sort);

        if(keyword!=null && !keyword.isBlank())
            return customerRepository.searchByKeyword(keyword.trim(), pageable);

        return customerRepository.findAll(pageable);
    }

    @Override
    public Customer saveCustomer(Customer customer) {
        Customer existedCustomerInDB = customerRepository.findById(customer.getId()).get();
        // In case don't want to change password :
        if(customer.getPassword().isEmpty()) {
            customer.setPassword(existedCustomerInDB.getPassword());
        }
        else {
            // In case change password :
            encodePassword(customer);
        }

        // Retain the first value of createdTime, verificationCode, authenticationType, resetPasswordToken when edit :
        customer.setCreatedTime(existedCustomerInDB.getCreatedTime());
        customer.setResetPasswordToken(existedCustomerInDB.getResetPasswordToken());
        customer.setAuthenticationType(existedCustomerInDB.getAuthenticationType());

        return customerRepository.save(customer);
    }

    private void encodePassword(Customer customer) {
        String rawPassword = customer.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        customer.setPassword(encodedPassword);
    }

    @Override
    public Customer getCustomerById(Integer id) throws CustomerNotFoundException {
        try {
            return customerRepository.findById(id).get();
        }
        catch (NoSuchElementException e) {
            throw new CustomerNotFoundException("Count not find any customer with id : "+id);
        }
    }

    @Override
    public void deleteCustomerById(Integer id) throws CustomerNotFoundException {
        Integer countResult = customerRepository.countById(id);

        // Check not find any brand
        if(countResult==null || countResult==0)
            throw new CustomerNotFoundException("Count not find any customer with id: "+id);

        // delete :
        customerRepository.deleteById(id);
    }

    @Override
    public void updateEnableStatus(Integer id, boolean enableStatus) throws CustomerNotFoundException {
        Integer countResult = customerRepository.countById(id);
        // Check not find any brand
        if(countResult==null || countResult==0)
            throw new CustomerNotFoundException("Count not find any customer with id: "+id);

        // update enable status
        customerRepository.updateEnableStatus(id, enableStatus);
    }


}
