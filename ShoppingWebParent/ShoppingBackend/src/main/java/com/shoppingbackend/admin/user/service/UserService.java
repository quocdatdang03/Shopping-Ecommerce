package com.shoppingbackend.admin.user.service;

import com.shopping.common.entity.User;
import com.shoppingbackend.admin.user.exception.UserNotFoundException;

import java.util.List;

public interface UserService {
    public List<User> findAll();
    public User getUserById(Integer id) throws UserNotFoundException;
    public void save(User user);
    public boolean isEmailUnique(Integer id, String email);
    public void delete(int id) throws UserNotFoundException;
    public void updateUserEnabledStatus(Integer id, boolean enabledStatus);
}
