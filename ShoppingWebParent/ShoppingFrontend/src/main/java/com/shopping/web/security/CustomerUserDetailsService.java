package com.shopping.web.security;

import com.shopping.common.entity.Customer;
import com.shopping.web.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomerUserDetailsService implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // get User from DB by email :
        Customer customer = customerRepository.findCustomerByEmail(username);
        CustomerUserDetails customerUserDetails;
        if(customer!=null) {
            customerUserDetails = new CustomerUserDetails(customer);
            return customerUserDetails;
        }
        else
        {
            throw new UsernameNotFoundException("There is no user with email: "+username);
        }
    }
}
