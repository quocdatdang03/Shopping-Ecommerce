package com.shopping.web.customer.repository;

import com.shopping.common.entity.Customer;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface CustomerRepository extends CrudRepository<Customer, Integer> {

    // find Customer by email:
    public Customer findCustomerByEmail(String email);

    // find Customer by verification code:
    public Customer findCustomerByVerificationCode(String verificationCode);

    // enable customer and set verificationCode = null:
    @Query("UPDATE Customer c SET c.enabled=true, c.verificationCode=null WHERE c.id=?1")
    @Modifying
    public void enableCustomer(Integer customerId);

}
