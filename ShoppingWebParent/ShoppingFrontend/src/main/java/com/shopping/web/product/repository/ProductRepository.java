package com.shopping.web.product.repository;

import com.shopping.common.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProductRepository extends PagingAndSortingRepository<Product, Integer> {
    // Paginate all products by category id (include subcategory of that category)
    @Query("SELECT p FROM Product p JOIN Category c ON p.category.id = c.id " +
            "WHERE c.id = ?1 or c.allParentIds like %?2%")
    public Page<Product> listProductByCategory(Integer categoryId, String parentCategoryId, Pageable pageable);

    // get product by alias
    public Product findProductByAlias(String alias);

    // search product with keyword
    @Query("SELECT p FROM Product p JOIN Category c ON p.category.id = c.id "+
            "WHERE (c.id = ?1 OR c.allParentIds like %?2%) AND (CONCAT(p.name,' ',p.alias,' ',p.price) LIKE %?3%)")
    public Page<Product> searchProductWithKeyword(Integer categoryId, String parentCategoryId, String keyword, Pageable pageable);
}
