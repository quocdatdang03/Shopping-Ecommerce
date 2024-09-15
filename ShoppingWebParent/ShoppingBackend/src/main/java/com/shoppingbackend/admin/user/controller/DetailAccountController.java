package com.shoppingbackend.admin.user.controller;

import com.shopping.common.entity.User;
import com.shoppingbackend.admin.security.ShoppingUserDetails;
import com.shoppingbackend.admin.user.exception.UserNotFoundException;
import com.shoppingbackend.admin.user.service.UserServiceImpl;
import com.shoppingbackend.admin.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class DetailAccountController {

    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/account")
    // @AuthenticationPrincipal : dùng để lấy ra object UserDetails khi authentication success
    // (cụ thể là obj class custom của ta là ShoppingUserDetails)
    public String showAccountDetail(@AuthenticationPrincipal ShoppingUserDetails userDetails, Model model) {
        // obj userDetails : chứa các thông tin của user khi login thành công
        User user = userService.getUserByEmail(userDetails.getUsername());
        model.addAttribute("user", user);

        // set new firstName and lastName for UserDetails (if firstName and lastName of User have been updated):
        userDetails.setFirstName(user.getFirstName());
        userDetails.setLastName(user.getLastName());


        return "user/account_detail";
    }


    @PostMapping("/account/update")
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
            User savedUser = userService.updateAccountDetail(user);

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

            userService.updateAccountDetail(user);
        }


        redirectAttributes.addFlashAttribute("message", "Your account details have been updated");
        return "redirect:/account";

    }
}
