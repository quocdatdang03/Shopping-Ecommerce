package com.shoppingbackend.admin.user.repository;

import com.shopping.common.entity.Role;
import com.shopping.common.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;
//    private EntityManager entityManager; // dùng EntityManager cũng được

    @Test
    public void testCreateUserWithOneRole()
    {
        Role role = entityManager.find(Role.class, 1);
        User user = new User();
        user.setFirstName("Dang");
        user.setLastName("Quoc Dat");
        user.setPassword("dat03122003");
        user.setEmail("dat03122003@gmail.com");
        user.addRole(role);

        User savedUser = userRepository.save(user);

        Assertions.assertThat(user).isNotNull();
    }

    @Test
    public void testCreateUserWithMoreRoles()
    {
        Role role1 = entityManager.find(Role.class, 1);
        Role role2 = entityManager.find(Role.class, 3);
        User user = new User();
        user.setFirstName("David");
        user.setLastName("Quoc");
        user.setPassword("david123");
        user.setEmail("david@gmail.com");
        user.addRole(role1);
        user.addRole(role2);

        User savedUser = userRepository.save(user);

        Assertions.assertThat(user).isNotNull();
    }

    @Test
    public void testListAllUser()
    {
       Iterable<User> users = userRepository.findAll();
        users.forEach(item -> System.out.println(item));

       Assertions.assertThat(users).hasAtLeastOneElementOfType(User.class);
    }

    @Test
    public void testUpdateUser()
    {
        // update role :
        // Xóa role cũ đi rồi add role mới vào:
        Role oldRole = entityManager.find(Role.class, 2);

        User user = entityManager.find(User.class, 3);
        // Xóa role cũ đi :
        user.getRoles().remove(oldRole);

        // Thêm role mới vào:
        Role newRole = entityManager.find(Role.class, 3);
        user.addRole(newRole);

        // Update email and enabled:
        user.setEmail("quocdat@gmail.com");
        user.setEnabled(true);

        // update:
        userRepository.save(user);
    }

    @Test
    public void testCountUserById()
    {
        int id = 1;
        Integer result = userRepository.countById(id);
        Assertions.assertThat(result).isNotNull().isGreaterThan(0);
    }

    @Test
    public void testDeleteUser()
    {
        User user = new User();
        user.setId(3);
        userRepository.delete(user);
    }

    @Test
    public void testUpdateEnabledStatus()
    {
        int id =9;
        boolean enabled=true;
        userRepository.updateEnabledStatus(id,enabled);
    }
}
