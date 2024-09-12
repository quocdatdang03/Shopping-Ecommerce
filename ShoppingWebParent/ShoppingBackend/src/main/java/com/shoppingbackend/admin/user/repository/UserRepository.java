package com.shoppingbackend.admin.user.repository;

import com.shopping.common.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Integer> {
    public User findByEmail(String email);
    public Integer countById(int id);

    @Query("UPDATE User SET enabled=?2 where id=?1")
    @Modifying // indicating this query for update or delete
    @Transactional
    public void updateEnabledStatus(Integer id, boolean enabledStatus);

    /* search with keyword on fields (id, email, firstName, lastName) (integrating width sorting and paginating*/
    /* concat all fields together (by concat method of JPQL) and search on that concat field*/
    @Query("SELECT u FROM User u WHERE CONCAT(u.id, ' ', u.email, ' ', u.firstName, ' ', u.lastName) LIKE %?1%")
    public Page<User> searchByKeyword(String keyword, Pageable pageable);

}
