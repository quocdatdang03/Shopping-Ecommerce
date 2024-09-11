package com.shoppingbackend.admin.user.repository;

import com.shopping.common.entity.Role;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

@DataJpaTest
// by default thì Spring Data JPA test sẽ sử dụng Inmemory DB của H2 Database
// Do đó để sử dụng Real DB (MYSQL của ta) thì cho replace = NONE
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false) // by default thì @Rollback value=true --> thì các changes tới DB sẽ không được commit mà sẽ rollback -> do đó phải để value=false để nó commit changes
public class RoleRepositoryTests {

    @Autowired
    private RoleRepository roleRepository;


    @Test
    public void testCreateAnRole()
    {
        Role roleAdmin = new Role("Admin2", "Manage everything");

        Role result = roleRepository.save(roleAdmin);
        /* Để test xem result trả về có id > 0 (tức là insert vào DB thành công)
         * --> Thì mới pass unit test (successful) */
        Assertions.assertThat(result.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateAllRoles()
    {
        Role roleAdmin = new Role("Admin", "Manage everything");
        Role roleSalesPerson = new Role("Sales Person", "Manage product price, customers, shipping, orders and sales report");
        Role roleEditor = new Role("Editor", "Manage categories, brands, products, articles and menus");
        Role roleShipper = new Role("Shipper", "View products, orders and update order status");
        Role roleAssistant = new Role("Assistant", "Manage questions and reviews");

        roleRepository.saveAll(List.of(roleAdmin,roleSalesPerson,roleAssistant, roleShipper,roleEditor));
    }
}
