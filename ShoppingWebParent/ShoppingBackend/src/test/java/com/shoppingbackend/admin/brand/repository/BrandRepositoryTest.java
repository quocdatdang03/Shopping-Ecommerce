package com.shoppingbackend.admin.brand.repository;


import com.shopping.common.entity.Brand;
import com.shopping.common.entity.Category;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.HashSet;
import java.util.Set;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class BrandRepositoryTest {

    @Autowired
    private BrandRepository brandRepository;


    @Test
    public void testCreateBrand() {

        Category category1 = new Category();
        category1.setId(7);
        Category category2 = new Category();
        category2.setId(4);
        Set<Category> categorySet = new HashSet<>();
        categorySet.add(category1);
        categorySet.add(category2);

        Brand brand = new Brand("Apple", categorySet);

        Brand savedBrand = brandRepository.save(brand);

        Assertions.assertThat(savedBrand.getId()).isGreaterThan(0);
    }

    @Test
    public void testFindAllBrands() {
        Iterable<Brand> brands = brandRepository.findAll();
        brands.forEach(item -> System.out.println(item));
    }

    @Test
    public void testGetBrandById() {
        Brand brand = brandRepository.findById(1).get();
        System.out.println(brand);

        Assertions.assertThat(brand).isNotNull();
    }


}
