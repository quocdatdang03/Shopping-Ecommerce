package com.shoppingbackend.admin.brand.repository;

import com.shopping.common.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends PagingAndSortingRepository<Brand, Integer> {
    // count by id:
    public Integer countById(Integer id);

    // search with keyword:
    @Query("SELECT b FROM Brand b WHERE CONCAT(b.id,' ', b.name) LIKE %?1%")
    public Page<Brand> searchByKeyword(String keyword, Pageable pageable);
}
