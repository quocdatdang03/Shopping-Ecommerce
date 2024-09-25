package com.shoppingbackend.admin.product.repository;

import com.shopping.common.entity.Brand;
import com.shopping.common.entity.Category;
import com.shopping.common.entity.Product;
import com.shopping.common.entity.ProductDetail;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.core.parameters.P;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.List;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void testCreateProduct() {
        // get Brand and Cateogry Entity:
        Brand brand = testEntityManager.find(Brand.class, 4);
        Category category = testEntityManager.find(Category.class, 6);

        Product product = new Product();
        product.setName("HP Envy x360");
        product.setAlias("hp_envy_x360");
        product.setShortDescription("This is short description of HP Envy x360");
        product.setFullDescription("This is full description of HP Envy x360");
        product.setBrand(brand);
        product.setCategory(category);
        product.setPrice(23000);
        product.setCreatedTime(new Date());
        product.setUpdatedTime(new Date());

        Product savedProduct = productRepository.save(product);

        Assertions.assertThat(savedProduct.getId()).isNotNull().isGreaterThan(0);
    }

    @Test
    public void testGetAllProducts() {
        List<Product> productList = (List<Product>) productRepository.findAll();
        for(Product product : productList)
        {
            System.out.println(product);
        }

    }

    @Test
    public void testGetProductById() {
        Product product = productRepository.findById(2).get();
        System.out.println(product);

        Assertions.assertThat(product).isNotNull();

    }

    @Test
    public void testUpdateProduct() {
        Product product = productRepository.findById(2).get(); // persistent object

        // dirty checking
        product.setPrice(111111);
        // productRepository.save(product);

        Assertions.assertThat(product).isNotNull();
    }

    @Test
    public void testDeleteProduct() {
        Product product = productRepository.findById(4).get(); // persistent object

        productRepository.delete(product);
    }

    @Test
    public void testUpdateProductWithProductDetails() {
        Integer id = 10;
        Product product = productRepository.findById(id).get();

        // add detail:
        product.addProductDetail("Detail 1", "Value detail 1");
        product.addProductDetail("Detail 2", "Value detail 2");
        product.addProductDetail("Detail 3", "Value detail 3");

        Product savedProduct = productRepository.save(product);

        Assertions.assertThat(savedProduct.getDetails()).isNotEmpty();
    }


}
