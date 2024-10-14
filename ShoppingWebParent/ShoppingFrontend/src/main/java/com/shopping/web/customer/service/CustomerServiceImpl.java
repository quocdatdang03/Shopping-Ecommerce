package com.shopping.web.customer.service;

import com.shopping.common.entity.Country;
import com.shopping.common.entity.Customer;
import com.shopping.web.customer.repository.CustomerRepository;
import com.shopping.web.setting.repository.CountryRepository;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<Country> findAllCountry() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    @Override
    public boolean isUniqueEmail(String email) {
        Customer customer = customerRepository.findCustomerByEmail(email);
        return customer==null;
    }

    @Override
    public void registerCustomer(Customer customer) {
        // encode password of customer :
        encodePassword(customer);

        // disable customer account by default:
        customer.setEnabled(false);

        customer.setCreatedTime(new Date());

        // create random string to verification code:
        String verificationCode = RandomString.make(64);
        customer.setVerificationCode(verificationCode);

        System.out.println(verificationCode);

        // save customer to DB:
        customerRepository.save(customer);
    }

    private void encodePassword(Customer customer) {
        String encodedPassword = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(encodedPassword);
    }


    @Override
    public boolean verifyCustomerAccount(String verificationCode) {
        Customer customer = customerRepository.findCustomerByVerificationCode(verificationCode);
        if(customer==null || customer.isEnabled())
            return false;
        else {
            // enable customer and set verification Code = null:
            customerRepository.enableCustomer(customer.getId());
            return true;
        }
    }
}
