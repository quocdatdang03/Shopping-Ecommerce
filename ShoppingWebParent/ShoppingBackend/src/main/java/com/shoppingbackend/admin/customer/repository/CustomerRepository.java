package com.shoppingbackend.admin.customer.repository;

import com.shopping.common.entity.Brand;
import com.shopping.common.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Integer> {
    // count by id:
    public Integer countById(Integer id);

    // search with keyword:
    @Query("SELECT b FROM Customer b WHERE CONCAT(b.id,' ', b.firstName,' ',b.lastName,' ',b.email, ' ',b.city,' ',b.state,' ',b.country) LIKE %?1%")
    public Page<Customer> searchByKeyword(String keyword, Pageable pageable);

    // update enable status:
    @Query("UPDATE Customer c SET c.enabled=?2, c.verificationCode=null WHERE c.id=?1")
    @Modifying
    public void updateEnableStatus(Integer customerId, boolean enableStatus);
}
