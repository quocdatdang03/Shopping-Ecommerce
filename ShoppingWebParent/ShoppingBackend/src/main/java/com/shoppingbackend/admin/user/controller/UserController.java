package com.shoppingbackend.admin.user.controller;

import com.shopping.common.entity.Role;
import com.shopping.common.entity.User;
import com.shoppingbackend.admin.user.exception.UserNotFoundException;
import com.shoppingbackend.admin.user.service.*;
import com.shoppingbackend.admin.util.FileUploadUtil;
import org.apache.tomcat.util.http.fileupload.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
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

    @Autowired
    private UserCsvExporter userCsvExporter;

    @Autowired
    private UserExcelExporter userExcelExporter;

    @Autowired
    private UserPdfExporter userPdfExporter;


    // Tạo dữ liệu chung cho Model của các Request Handler
    @ModelAttribute("roleList")
    public List<Role> getRoleList()
    {
        List<Role> roleList = roleService.findAll();
        return roleList;
    }

    @GetMapping("")
    public String showFirstListUser(Model model)
    {
        String sortField = "id";
        String sortDir = "asc";
        String keyword = null;
        return listByPage(1,sortField,sortDir, keyword, model);
    }

    // PAGINATION
    @GetMapping("/page/{pageNumber}")
    public String listByPage(
            @PathVariable("pageNumber") int pageNumber,
            @RequestParam(value = "sortField") String sortField,
            @RequestParam(value = "sortDir") String sortDir,
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model)
    {
        // Handle paginate and sorting and search by keyword  :
        Page<User> page = userService.listByPage(pageNumber, sortField,sortDir,keyword);

        // get information for pagination:
        int totalPages = page.getTotalPages();
        int userNumberPerPage = page.getNumberOfElements(); // Lấy ra số item hiển thị trên 1 trang
        long totalUsers = page.getTotalElements();
        int firstPageNumber = ((pageNumber-1)*userService.USER_NUMBER_PER_PAGE)+1;
        int lastPageNumber = pageNumber*userService.USER_NUMBER_PER_PAGE;
        if(lastPageNumber>totalUsers)
            lastPageNumber = (int) totalUsers;

        // information for sorting:
        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";


        List<User> users = page.getContent();

        model.addAttribute("firstPageNumber", firstPageNumber);
        model.addAttribute("lastPageNumber", lastPageNumber);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("userList", users);

        // information for sorting:
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);

        // information for search by keyword:
        model.addAttribute("keyword", keyword);

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


        redirectAttributes.addFlashAttribute("message", "The User has been saved successfully");

        String redirectUrlToAffectedUser = "redirect:/users/page/1?sortField=id&sortDir=asc&keyword="+user.getEmail();
        return redirectUrlToAffectedUser;
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
        String message;
        if(enabledStatus==true)
            message = "Activate User (id:"+id+") successfully!";
        else
            message = "Deactivate User (id:"+id+") successfully!";
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/users";
    }

    // EXPORT List User to CSV:
    @GetMapping("/export/csv")
    public void exportToCsv(HttpServletResponse response) throws IOException {
        // get all list users:
        List<User> users = userService.findAll();

        // handle export list user to csv file:
        userCsvExporter.export(users, response);
    }

    // EXPORT List User to CSV:
    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        // get all list users:
        List<User> users = userService.findAll();

        // handle export list user to excel file:
        userExcelExporter.export(users, response);
    }

    // EXPORT List User to PDF:
    @GetMapping("/export/pdf")
    public void exportToPdf(HttpServletResponse response) throws IOException {
        // get all list users:
        List<User> users = userService.findAll();

        // handle export list user to pdf file:
        userPdfExporter.export(users, response);
    }


    // HANDLE EXCEPTION:
    @ExceptionHandler(value = UserNotFoundException.class)
    public String handleNoSuchElementException(UserNotFoundException userNotFoundException,RedirectAttributes redirectAttributes)
    {
        redirectAttributes.addFlashAttribute("messageError", userNotFoundException.getMessage());
        return "redirect:/users";
    }
}
