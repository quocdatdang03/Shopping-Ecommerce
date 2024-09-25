package com.shoppingbackend.admin.brand.controller;

import com.shopping.common.entity.Brand;
import com.shopping.common.entity.Category;
import com.shoppingbackend.admin.brand.exception.BrandNotFoundException;
import com.shoppingbackend.admin.brand.service.BrandServiceImpl;
import com.shoppingbackend.admin.category.service.CategoryServiceImpl;
import com.shoppingbackend.admin.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/brands")
public class BrandController {

    @Autowired
    private BrandServiceImpl brandService;

    @Autowired
    private CategoryServiceImpl categoryService;

    @ModelAttribute("categoryList")
    public List<Category> getCategoryList() {
        List<Category> categories = categoryService.listCategoryInForm();
        return categories;
    }

    @GetMapping("")
    public String listFirstPage(Model model) {
        List<Brand> brandList = brandService.listAllBrands();
        model.addAttribute("brands", brandList);

        return listBrandByPage(1,"asc","name",null, model);
    }

    @GetMapping("/page/{pageNumber}")
    public String listBrandByPage(
            @PathVariable("pageNumber") Integer pageNumber,
            @Param("sortDir") String sortDir,
            @Param("sortField") String sortField,
            @Param("keyword") String keyword,
            Model model
    ) {
        if(sortDir==null || sortDir.isEmpty())
            sortDir = "asc";
        if(sortField==null || sortField.isEmpty())
            sortField = "name";

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        Page<Brand> pageResult = brandService.listBrandsByPage(pageNumber, sortField, sortDir, keyword);

        // get information for pagination:
        List<Brand> brandList = pageResult.getContent();
        int totalPages = pageResult.getTotalPages();
        long totalElements = pageResult.getTotalElements();
        int firstPageNumber = ((pageNumber-1)*brandService.BRAND_NUMBER_PER_PAGE)+1;
        int lastPageNumber = pageNumber * brandService.BRAND_NUMBER_PER_PAGE;
        if(lastPageNumber>=totalElements)
            lastPageNumber = (int) totalElements;

        model.addAttribute("brands", brandList);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalElements", totalElements);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("sortField", sortField);
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("keyword", keyword);
        model.addAttribute("firstPageNumber", firstPageNumber);
        model.addAttribute("lastPageNumber", lastPageNumber);

        return "brand/brand";
    }

    @GetMapping("/new")
    public String showBrandForm(@ModelAttribute("brand") Brand brand, Model model) {
        model.addAttribute("pageTitle","Create New Brand");
        return "brand/brand_form";
    }

    // SAVE : CREATE and UPDATE:
    @PostMapping("/save")
    public String saveBrand(
            @ModelAttribute("brand") Brand brand,
            @RequestParam("inputLogoFile") MultipartFile multipartFile,
            RedirectAttributes redirectAttributes) throws IOException
    {
        // in case file input has uploaded file:
        if(!multipartFile.isEmpty()) {
            // get uploaded file from file input:
            String nameUploadedFile = StringUtils.cleanPath(multipartFile.getOriginalFilename());

            brand.setLogo(nameUploadedFile);
            // save brand to DB:
            Brand savedBrand = brandService.saveBrand(brand);

            // create name dir : brand-images/<Id of Brand just saved >
            String nameUploadDir = "../brand-images/"+savedBrand.getId();

            // clean files in advanced of dir :
            FileUploadUtil.cleanDirectory(nameUploadDir);

            // Then upload new file to dir :
            FileUploadUtil.uploadFileToLocalDirectory(nameUploadDir, nameUploadedFile, multipartFile);
        }
        else {
            brandService.saveBrand(brand);
        }

        redirectAttributes.addFlashAttribute("message","The Brand( name: "+brand.getName()+" ) has been saved successfully!");

        return "redirect:/brands/page/1?keyword="+brand.getName();
    }

    // SHOW EDIT FORM :
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model) throws BrandNotFoundException {
        // get brand by id :
        Brand brand;
        try {
            brand = brandService.getBrandById(id);
            Set<Category> categories = brand.getCategories();
            model.addAttribute("categoryListOfBrand", categories);


        } catch (BrandNotFoundException e) {
            throw new BrandNotFoundException(e.getMessage());
        }

        model.addAttribute("brand", brand);
        model.addAttribute("pageTitle", "Edit Brand (ID:"+id+")");

        return "brand/brand_form";
    }

    // DELETE
    @GetMapping("/delete/{id}")
    public String deleteByid(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) throws BrandNotFoundException {
        // delete :
        try {
            brandService.deleteBrandById(id);
            // after delete brand, remove image dir of that brand:
            String imageBrandDirPath = "../brand-images/"+id;
            FileUploadUtil.removeDirectory(imageBrandDirPath);

            redirectAttributes.addFlashAttribute("message", "The Brand(id: "+id+") has been deleted successfully");
            return "redirect:/brands";
        } catch (BrandNotFoundException e) {
            throw new BrandNotFoundException(e.getMessage());
        }
    }

    // HANDLE EXCEPTION :
    @ExceptionHandler(value = BrandNotFoundException.class)
    public String handleBrandNotFoundException(BrandNotFoundException brandNotFoundException, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("messageError", brandNotFoundException.getMessage());
        return "redirect:/brands";
    }

}
