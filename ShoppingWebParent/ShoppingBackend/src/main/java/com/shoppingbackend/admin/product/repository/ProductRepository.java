package com.shoppingbackend.admin.product.repository;

import com.shopping.common.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // search with keyword :
    @Query("SELECT p FROM Product p WHERE CONCAT(p.id,' ', p.name, ' ', p.brand.name, ' ', p.category.name) LIKE %?1%")
    public Page<Product> searchWithKeyword(String keyword, Pageable pageable);

    // filter by category (include subcategory of that category):
    @Query("SELECT p FROM Product p JOIN Category c ON p.category.id = c.id WHERE c.id = ?1 OR c.allParentIds LIKE %?2%")
    public Page<Product> findAllProductByCategory(Integer categoryId, String parentCategoryId, Pageable pageable);

    // filter by category and keyword of search box :
    @Query("SELECT p FROM Product p JOIN Category c ON p.category.id = c.id " +
            "WHERE (c.id = ?1 OR c.allParentIds LIKE %?2%) " +
            "AND (CONCAT(p.id,' ', p.name, ' ', p.brand.name, ' ', p.category.name) LIKE %?3%)")
    public Page<Product> searchWithKeywordAndFilterByCategory(Integer categoryId, String parentCategoryId, String keyword, Pageable pageable);
}
