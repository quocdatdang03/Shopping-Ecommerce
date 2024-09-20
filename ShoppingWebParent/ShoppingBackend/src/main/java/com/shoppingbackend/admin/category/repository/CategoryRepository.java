package com.shoppingbackend.admin.category.repository;

import com.shopping.common.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface CategoryRepository extends PagingAndSortingRepository<Category, Integer> {
    @Query("SELECT c FROM Category c where c.parent.id is null ")
    public List<Category> listRootCategories(Sort sort);

    // Pagination:
    @Query("SELECT c FROM Category c where c.parent.id is null ")
    public Page<Category> listRootCategories(Pageable pageable);

    @Query("Update Category c set c.enabled=?1 where c.id=?2")
    @Modifying
    @Transactional
    public void updateEnabledStatus(boolean enabledStatus, Integer id);

    public int countById(Integer id);

    // search with keyword
    @Query("SELECT c FROM Category c WHERE CONCAT(c.id, ' ', c.name, ' ', c.alias, ' ', c.enabled) LIKE %?1%")
    public Page<Category> searchWithKeyword(String keyword, Pageable pageable);
}
