package com.shoppingbackend.admin.user.controller;

import com.shopping.common.entity.Role;
import com.shopping.common.entity.User;
import com.shoppingbackend.admin.user.exception.UserNotFoundException;
import com.shoppingbackend.admin.user.service.RoleServiceImpl;
import com.shoppingbackend.admin.user.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private RoleServiceImpl roleService;

    // Tạo dữ liệu chung cho Model của các Request Handler
    @ModelAttribute("roleList")
    public List<Role> getRoleList()
    {
        List<Role> roleList = roleService.findAll();
        return roleList;
    }

    @GetMapping("")
    public String showListUser(Model model)
    {
        List<User> users = userService.findAll();
        model.addAttribute("userList", users);
        return "user/user";
    }

    // NEW
    @GetMapping("/new")
    public String showUserCreateForm(@ModelAttribute("user") User user, Model model)
    {
        model.addAttribute("pageTitle", "Create New User");
        return "user/user_form";
    }

    // EDIT with ID:
    @GetMapping("/edit/{id}")
    public String showUserEditForm(@PathVariable("id") Integer id, Model model) throws UserNotFoundException {
        User user;
        try
        {
            user = userService.getUserById(id);
        }
        catch (UserNotFoundException ex)
        {
            throw new UserNotFoundException(ex.getMessage());
        }

        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Edit User (ID:"+user.getId()+")");
        return "user/user_form";
    }

    // SAVE
    @PostMapping("/save")
    public String saveUser(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes)
    {
        userService.save(user);
        redirectAttributes.addFlashAttribute("message", "The User has been saved successfully");

        return "redirect:/users";
    }

    // DELETE
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") int id, RedirectAttributes redirectAttributes) throws UserNotFoundException {
        try {
            userService.delete(id);
            redirectAttributes.addFlashAttribute("message", "The User(id: "+id+") has been deleted successfully");
            return "redirect:/users";
        } catch (UserNotFoundException ex) {
            throw new UserNotFoundException(ex.getMessage());
        }
    }

    // UPDATE ENABLED STATUS:
    @GetMapping("/{userId}/enabled/{enabledStatus}")
    public String updateEnabledStatus(@PathVariable("userId") Integer id,
                                      @PathVariable("enabledStatus") boolean enabledStatus,
                                      RedirectAttributes redirectAttributes)
    {
        userService.updateUserEnabledStatus(id,enabledStatus);
        redirectAttributes.addFlashAttribute("message", "Enabled Status of User (id:"+id+") has been updated successfully!");
        return "redirect:/users";
    }


    // HANDLE EXCEPTION:
    @ExceptionHandler(value = UserNotFoundException.class)
    public String handleNoSuchElementException(UserNotFoundException userNotFoundException,RedirectAttributes redirectAttributes)
    {
        redirectAttributes.addFlashAttribute("messageError", userNotFoundException.getMessage());
        return "redirect:/users";
    }
}
