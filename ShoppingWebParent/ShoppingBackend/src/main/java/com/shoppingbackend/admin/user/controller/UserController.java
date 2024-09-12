package com.shoppingbackend.admin.user.controller;

import com.shopping.common.entity.Role;
import com.shopping.common.entity.User;
import com.shoppingbackend.admin.user.exception.UserNotFoundException;
import com.shoppingbackend.admin.user.service.RoleServiceImpl;
import com.shoppingbackend.admin.user.service.UserServiceImpl;
import com.shoppingbackend.admin.util.FileUploadUtil;
import org.apache.tomcat.util.http.fileupload.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
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
    public String saveUser(@ModelAttribute("user") User user,
                           RedirectAttributes redirectAttributes,
                           @RequestParam("inputFileImage") MultipartFile multipartFile) throws IOException, UserNotFoundException {

        // get file name from multipart file:
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        // if input has upload file
        if(!multipartFile.isEmpty())
        {
            // save user to DB:
            user.setPhotos(fileName);
            User savedUser = userService.save(user);

            // create directory : user-images/<userId>
            String uploadDir = "user-images/"+savedUser.getId();

            // Trước khi uploadFileToLocalDirectory thì phải xóa toàn bộ file trước đó (chỉ lưu 1 file image duy nhất):
            FileUploadUtil.cleanDirectory(uploadDir);
            // save uploaded file to directory
            FileUploadUtil.uploadFileToLocalDirectory(uploadDir, fileName, multipartFile);
        }
        else
        {
            // Nếu không upload file thì set photos null
            if (user.getPhotos().isEmpty())
                user.setPhotos(null);

            userService.save(user);
        }


//
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
