package com.shoppingbackend.admin.product.repository;

import com.shopping.common.entity.Product;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, Integer> {

    // updated enabled Status :
    @Query("Update Product p set p.enabled=?1 where p.id=?2")
    @Modifying
    @Transactional
    public void updateEnabledStatus(boolean enabledStatus, Integer id);

    public Integer countById(Integer id);
}
