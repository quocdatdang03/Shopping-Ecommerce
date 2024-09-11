package com.shoppingbackend.admin.user.service;

import com.shopping.common.entity.User;
import com.shoppingbackend.admin.user.exception.UserNotFoundException;
import com.shoppingbackend.admin.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public List<User> findAll() {
        return (List<User>) userRepository.findAll();
    }

    @Override
    public User getUserById(Integer id) throws UserNotFoundException {
        try {
            return userRepository.findById(id).get();
        }
        catch(NoSuchElementException ex)
        {
            throw new UserNotFoundException("Could not find any User with id: "+id +"!");
        }

    }

    // SAVE : CREATE OR EDIT USER
    @Override
    public void save(User user) {

        // if in case Create user:
        if(user.getId()==null)
        {
            encodePassword(user);
        }
        else {
            // if in case Edit user:
            // Bên HTML khi edit thì inpyt type=password sẽ để trống giá trị để bảo mật (không hiển thị value của nó trong DB)
            // Trong TH client không nhập lại gì vào ô input password
            // Do đó ta phải lấy password cùa user đó trong DB ra và set lại cho nó
            if(user.getPassword().isEmpty())
            {
                User userExistInDB = userRepository.findById(user.getId()).get();
                user.setPassword(userExistInDB.getPassword());
            }
            else
            {
                // Trong TH client password is provided thì encode password
                encodePassword(user);
            }
        }

        userRepository.save(user);
    }

    // DELETE USER:
    @Override
    public void delete(int id) throws UserNotFoundException {
        Integer resultCount = userRepository.countById(id);
        if(resultCount==null || resultCount==0)
            throw new UserNotFoundException("Could not find any User with id: "+id +"!");
        else
            userRepository.deleteById(id);
    }


    // ENCODE PASSWORD :
    // method for encoding password :
    private void encodePassword(User user)
    {
        String rawPassword = user.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        user.setPassword(encodedPassword);
    }

    // CHECK UNIQUE EMAIL:
    // check whether email exists in database yet:
    // if not yet -> return true  else return false
    @Override
    public boolean isEmailUnique(Integer id,String email) {
        User userByEmail = userRepository.findByEmail(email);
        /*
            userByEmail != null -> email exists in DB
            userByEmail == null -> email doesn't exist in DB
         */

        if(userByEmail==null) // if email doesn't exist in DB
            return true;

        // Nếu đang ở case Create User:
        if(id==null)
        {
            if(userByEmail!=null) // if email exists in DB
                return false;
            else
                return true;
        }
        else {
            // Nếu đang ở case Edit User
            if(id == userByEmail.getId()) // ìf email vẫn chính là email hiện tại của User đang được edit
                return true;
            if(userByEmail!=null)
                return false;
            else
                return true;
        }
    }

    // UPDATE ENABLED STATUS:
    @Override
    public void updateUserEnabledStatus(Integer id, boolean enabledStatus) {
        userRepository.updateEnabledStatus(id, enabledStatus);
    }

}
