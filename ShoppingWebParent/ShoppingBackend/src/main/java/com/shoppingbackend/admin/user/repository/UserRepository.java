package com.shoppingbackend.admin.user.repository;

import com.shopping.common.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    public User findByEmail(String email);
    public Integer countById(int id);

    @Query("UPDATE User SET enabled=?2 where id=?1")
    @Modifying // indicating this query for update or delete
    @Transactional
    public void updateEnabledStatus(Integer id, boolean enabledStatus);
}
