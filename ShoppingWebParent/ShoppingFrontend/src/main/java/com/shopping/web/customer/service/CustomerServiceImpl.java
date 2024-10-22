package com.shopping.web.customer.service;

import com.shopping.common.entity.AuthenticationType;
import com.shopping.common.entity.Country;
import com.shopping.common.entity.Customer;
import com.shopping.common.exception.CustomerNotFoundException;
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
    public Customer getCustomerByEmail(String email) {
        return customerRepository.findCustomerByEmail(email);
    }

    @Override
    public Customer getCustomerByResetPasswordToken(String resetPasswordToken) {
        return customerRepository.findCustomersByResetPasswordToken(resetPasswordToken);
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

        customer.setAuthenticationType(AuthenticationType.DATABASE);

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

    @Override
    public void resetPassword(String newPassword, String resetPasswordToken) throws CustomerNotFoundException {
        Customer customer = customerRepository.findCustomersByResetPasswordToken(resetPasswordToken);
        if(customer==null)
            throw new CustomerNotFoundException("Could not find any Customer with reset password token: "+resetPasswordToken);


        // update new password :
        customer.setPassword(newPassword);
        encodePassword(customer);

        // set resetPasswordToken = null
        customer.setResetPasswordToken(null);

        customerRepository.save(customer);

    }

    @Override
    public void updateAuthenticationType(Customer customer, AuthenticationType authType) {
        if(customer.getId()!=null && customer.getId()>0) {
            // check authType is different with authenticationType of customer --> Update authenticationType of Customer = authType
            if(!customer.getAuthenticationType().equals(authType)) {
                customerRepository.updateAuthenticationType(authType, customer.getId());
            }
        }
    }

    @Override
    public void addNewCustomerUponOAuthLogin(String name, String email, String countryCode) {
        Customer newCustomer = new Customer();
        newCustomer.setEmail(email);
        setFirstNameAndLastName(name, newCustomer);

        // cho mặc định các field khác là empty:
        newCustomer.setPassword("");
        newCustomer.setEnabled(true);
        newCustomer.setCreatedTime(new Date());
        newCustomer.setPhoneNumber("");
        newCustomer.setAddressLine1("");
        newCustomer.setCity("");
        newCustomer.setState("");
        newCustomer.setPostalCode("");
        newCustomer.setAuthenticationType(AuthenticationType.GOOGLE);

        // country code : mặc định được get từ request.getLocale().getCountry()
        newCustomer.setCountry(countryRepository.findByCode(countryCode));

        customerRepository.save(newCustomer);
    }


    private void setFirstNameAndLastName(String name, Customer customer) {
        String []arrayWords = name.split(" ");
        if(arrayWords.length<2) {
            customer.setFirstName(name);
            customer.setLastName("");
        }
        else {
            String firstName = arrayWords[0];
            String lastName = name.substring(name.indexOf(" ")+1);
            customer.setFirstName(firstName);
            customer.setLastName(lastName);
        }
    }

    @Override
    public String updateResetPasswordToken(String email) throws CustomerNotFoundException {
        Customer customerInDB = customerRepository.findCustomerByEmail(email);
        if(customerInDB!=null) {
            // handle generate resetPasswordToken with 30 chars:
            String resetPasswordToken = RandomString.make(30);
            customerInDB.setResetPasswordToken(resetPasswordToken);

            // save changes:
            customerRepository.save(customerInDB);
            return resetPasswordToken;
        }
        else {
            throw new CustomerNotFoundException("Could not find any customer with email: "+email);
        }
    }
}
