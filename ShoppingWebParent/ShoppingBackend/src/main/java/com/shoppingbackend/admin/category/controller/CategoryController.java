package com.shoppingbackend.admin.category.controller;

import com.shopping.common.entity.Category;
import com.shopping.common.entity.Role;
import com.shopping.common.entity.User;
import com.shopping.common.exception.CategoryNotFoundException;
import com.shoppingbackend.admin.category.dto.CategoryPageInfo;
import com.shoppingbackend.admin.category.exporter.CategoryCsvExporter;
import com.shoppingbackend.admin.category.service.CategoryServiceImpl;
import com.shoppingbackend.admin.user.exception.UserNotFoundException;
import com.shoppingbackend.admin.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryServiceImpl categoryService;

    @Autowired
    private CategoryCsvExporter categoryCsvExporter;

    // Tạo dữ liệu chung cho Model của các Request Handler
    @ModelAttribute("categoryList")
    public List<Category> getCategoryList()
    {
        List<Category> listCategoryInForm = categoryService.listCategoryInForm();
        return listCategoryInForm;
    }

    @GetMapping("")
    public String listFirstPage(Model model) {
        return listByPage(1,"asc", null,model);
    }

    /* Chỉ phân trang cho root category (category mà có parent_id = null */
    @GetMapping("/page/{pageNumber}")
    public String listByPage(
            @PathVariable("pageNumber") Integer pageNumber,
            @Param("sortDir") String sortDir,
            @Param("keyword") String keyword,
            Model model) {

        if(sortDir==null || sortDir.isEmpty())
            sortDir="asc";
        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        // obj categoryPageInfo này ta chỉ dùng để làm trung gian để get dữ liệu page info từ Service sang Controller
        CategoryPageInfo categoryPageInfo = new CategoryPageInfo();

        List<Category> categories = categoryService.findAll(pageNumber, sortDir, categoryPageInfo, keyword);
        model.addAttribute("categories", categories);

        // attributes for sorting:
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("sortDir", sortDir);

        // attributes for paginating :
        int totalPages = categoryPageInfo.getTotalPages();
        long totalElements = categoryPageInfo.getTotalElements();
        int firstPageNumber = ((pageNumber-1)*categoryService.ROOT_CATEGORY_PER_PAGE)+1;
        int lastPageNumber = pageNumber*categoryService.ROOT_CATEGORY_PER_PAGE;
        if(lastPageNumber>totalElements)
            lastPageNumber = (int) totalElements;

        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalElements", totalElements);
        model.addAttribute("firstPageNumber", firstPageNumber);
        model.addAttribute("lastPageNumber", lastPageNumber);

        // attributes for sorting :
        model.addAttribute("keyword", keyword);

        return "category/category";
    }

    @GetMapping("/new")
    public String showCategoryForm(@ModelAttribute("category") Category category, Model model) {
        model.addAttribute("pageTitle", "Create New Category");
        return "category/category_form";
    }

    // SAVE (CREATE AND EDIT):
    @PostMapping("/save")
    public String saveCategory(@ModelAttribute("category") Category category,
                               @RequestParam("inputFileImage") MultipartFile multipartFile,
                               RedirectAttributes redirectAttributes) throws IOException {
        // get image file name from multipart file:
        String imageFileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

        // if input has upload file
        if(!multipartFile.isEmpty())
        {
            category.setImage(imageFileName);
            // save category to DB:
            Category savedCategory = categoryService.saveCategory(category);

            // create directory: category-images/<category id>
            // Cho directory này nằm cùng cấp với ShoppingBackend và ShoppingFrontend :
            String uploadDir = "../category-images/"+savedCategory.getId();

            // Trước khi uploadFileToLocalDirectory thì phải xóa toàn bộ file trước đó (chỉ lưu 1 file image duy nhất):
            FileUploadUtil.cleanDirectory(uploadDir);
            // save uploaded file to directory
            FileUploadUtil.uploadFileToLocalDirectory(uploadDir, imageFileName, multipartFile);
        }
        else {
            categoryService.saveCategory(category);
        }


        redirectAttributes.addFlashAttribute("message", "The category has been saved successfully");

        return "redirect:/categories/page/1?keyword="+category.getName()+" "+category.getAlias();

    }

    // SHOW EDIT :
    @GetMapping("/edit/{id}")
    public String showCategoryEditForm(@PathVariable("id") Integer id, Model model) throws CategoryNotFoundException {
        Category category;
        try {
            category = categoryService.getCategoryById(id);
        } catch (CategoryNotFoundException e) {
            throw new CategoryNotFoundException(e.getMessage());
        }


        model.addAttribute("category", category);
        model.addAttribute("pageTitle", "Edit Category (ID:"+category.getId()+")");
        return "category/category_form";
    }

    // UPDATE ENABLED Status :
    @GetMapping("/{id}/enabled/{enabledStatus}")
    public String updateEnabledStatus(
            @PathVariable("enabledStatus") boolean enabledStatus,
            @PathVariable("id") Integer id,
            RedirectAttributes redirectAttributes) {

        categoryService.updateEnabledStatus(enabledStatus, id);

        String message;
        if(enabledStatus==true)
            message = "Activate Category (id:"+id+") successfully!";
        else
            message = "Deactivate Category (id:"+id+") successfully!";
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/categories";
    }

    // DELETE
    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) throws CategoryNotFoundException {
        try {
            categoryService.deleteCategory(id);
            // after delete category, remove image dir of that category:
            String imageCategoryDirPath = "../category-images/"+id;
            FileUploadUtil.removeDirectory(imageCategoryDirPath);
            redirectAttributes.addFlashAttribute("message", "The Category(id: "+id+") has been deleted successfully");
            return "redirect:/categories";

        } catch (CategoryNotFoundException e) {
            throw new CategoryNotFoundException(e.getMessage());
        }
    }

    // EXPORT :
    // Export to CSV file:
    @GetMapping("/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        // get all categories list in hierarchical form:
        List<Category> categories = categoryService.listCategoryInForm();

        // export categories list to csv:
        categoryCsvExporter.export(categories, response);
    }

    // HANDLE EXCEPTION:
    @ExceptionHandler(value = CategoryNotFoundException.class)
    public String handleNoSuchElementException(CategoryNotFoundException categoryNotFoundException,RedirectAttributes redirectAttributes)
    {
        redirectAttributes.addFlashAttribute("messageError", categoryNotFoundException.getMessage());
        return "redirect:/categories";
    }

}
