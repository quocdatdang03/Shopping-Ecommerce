package com.shopping.web.customer.repository;

import com.shopping.common.entity.Country;
import com.shopping.common.entity.Customer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Date;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void testCreateCustomer() {
        Country country = new Country(242); // VN

        Customer customer = new Customer();
        customer.setCountry(country);
        customer.setFirstName("Dang");
        customer.setLastName("Quoc Dat");
        customer.setPassword("dat03122003");
        customer.setEmail("dat03122003@gmail.com");
        customer.setPhoneNumber("0944198125");
        customer.setAddressLine1("K72/29/10 Dinh Tien Hoang, Hai Chau");
        customer.setCity("Da Nang");
        customer.setState("Da Nang city");
        customer.setPostalCode("9999");
        customer.setCreatedTime(new Date());

        Customer savedCustomer = customerRepository.save(customer);

        Assertions.assertThat(savedCustomer).isNotNull();
        Assertions.assertThat(savedCustomer.getId()).isGreaterThan(0);
    }

}
