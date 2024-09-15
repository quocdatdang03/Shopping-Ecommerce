package com.shoppingbackend.admin.user.service;

import com.shopping.common.entity.User;
import com.shoppingbackend.admin.user.exception.UserNotFoundException;
import com.shoppingbackend.admin.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserServiceImpl implements UserService{
    public static final int USER_NUMBER_PER_PAGE = 5;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public List<User> findAll() {
        return (List<User>) userRepository.findAll();
    }

    @Override
    public Page<User> listByPage(int pageOffset, String sortField, String sortDir, String keyword) {
        Sort sort = Sort.by(sortField);
        sort = (sortDir.equals("asc")) ? sort.ascending() : sort.descending();

        int pageNumber = pageOffset;

        // Do DB có record bắt đầu từ index 0 -> ta phải trừ đi 1
        Pageable pageable = PageRequest.of(pageNumber-1,USER_NUMBER_PER_PAGE,sort);

        if(keyword!=null)
            return userRepository.searchByKeyword(keyword, pageable);
        else
            return userRepository.findAll(pageable);
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

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // SAVE : CREATE OR EDIT USER
    @Override
    public User save(User user) {

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

        return userRepository.save(user);
    }

    // UPDATE ACCOUNT DETAIL of USER:
    @Override
    public User updateAccountDetail(User userInForm) {
        User userInDb = userRepository.findById(userInForm.getId()).get();

        // check if password of user in form is empty -> Keep current password in DB
        if(userInForm.getPassword().isEmpty()) {
            userInForm.setPassword(userInDb.getPassword());
        }
        else {
            encodePassword(userInForm);
        }

        return userRepository.save(userInForm);
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
