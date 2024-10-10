package com.shopping.web.category.repository;

import com.shopping.common.entity.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Integer> {

    // find all enabled Category :
    @Query("SELECT c FROM Category c WHERE c.enabled=true")
    public List<Category> findALlEnabledCategory();

    // find enabled Category by alias :
    @Query("SELECT c FROM Category c WHERE c.alias = ?1 and c.enabled=true")
    public Category findEnabledCategoryByAlias(String alias);
}
