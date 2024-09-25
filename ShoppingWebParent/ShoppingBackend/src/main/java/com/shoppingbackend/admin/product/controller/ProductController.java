package com.shoppingbackend.admin.product.controller;

import com.shopping.common.entity.Brand;
import com.shopping.common.entity.Category;
import com.shopping.common.entity.Product;
import com.shoppingbackend.admin.brand.service.BrandService;
import com.shoppingbackend.admin.brand.service.BrandServiceImpl;
import com.shoppingbackend.admin.category.exception.CategoryNotFoundException;
import com.shoppingbackend.admin.category.service.CategoryServiceImpl;
import com.shoppingbackend.admin.product.exception.ProductNotFoundException;
import com.shoppingbackend.admin.product.service.ProductServiceImpl;
import com.shoppingbackend.admin.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private CategoryServiceImpl categoryService;

    @Autowired
    private BrandServiceImpl brandService;

    @ModelAttribute("categoryList")
    public List<Category> getProductListInHierarchicalForm() {
        List<Category> categoryList = categoryService.listCategoryInForm();
        return categoryList;
    }

    @ModelAttribute("brandList")
    public List<Brand> getBrandList() {
        List<Brand> brandList = brandService.listAllBrands();
        return brandList;
    }

    @GetMapping("")
    public String listFirstPage(Model model)
    {
        List<Product> productList = productService.listAllProducts();
        model.addAttribute("products", productList);
        return "product/product";
    }

    @GetMapping("/new")
    public String showProductForm(@ModelAttribute("product") Product product, Model model) {
        // by default set enabled and inStock are true :
        product.setEnabled(true);
        product.setInStock(true);

        model.addAttribute("pageTitle", "Create New Product");
        return "product/product_form";
    }

    // SAVE : CREATE OR UPDATE :
    @PostMapping("/save")
    public String saveProduct(
            @ModelAttribute("product") Product product,
            @RequestParam("inputImageFile") MultipartFile mainImageMultipartFile,
            @RequestParam("inputExtraImageFile") MultipartFile[] extraImagesMultipartFiles,
            RedirectAttributes redirectAttributes) throws IOException {

        // set field name for Entity Product and ProductImage:
        setMainImageName(mainImageMultipartFile, product);
        setExtraImageNames(extraImagesMultipartFiles, product);

        // save product to DB:
        Product savedProduct = productService.saveProduct(product);


        saveUploadedImage(mainImageMultipartFile, extraImagesMultipartFiles, savedProduct);

        redirectAttributes.addFlashAttribute("message","The Product( name: "+product.getName()+" ) has been saved successfully!");

        return "redirect:/products";
    }


    private void setMainImageName(MultipartFile multipartFile, Product product) {
        // in case file input has uploaded file:
        if(!multipartFile.isEmpty()) {
            // get uploaded file from file input:
            String nameUploadedFile = StringUtils.cleanPath(multipartFile.getOriginalFilename());

            product.setMainImage(nameUploadedFile);
        }
    }

    private void setExtraImageNames(MultipartFile[] multipartFiles, Product product) {
        if(multipartFiles.length > 0)
        {
            for(MultipartFile multipartFile : multipartFiles) {
                // Nếu multipartFile mà empty thì add nó vào extraImages
                if(multipartFile.isEmpty())
                    continue;
                // get uploaded file from file input:
                String nameUploadedFile = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                product.addExtraImages(nameUploadedFile);
            }
        }
    }

    private void saveUploadedImage(
            MultipartFile mainImageMultipartFile,
            MultipartFile[] extraImageMultipartFiles,
            Product savedProduct) throws IOException {
        // in case inputImageFile has uploaded file:
        if(!mainImageMultipartFile.isEmpty()) {
            // get uploaded file from file input:
            String nameUploadedFile = StringUtils.cleanPath(mainImageMultipartFile.getOriginalFilename());

            // create name dir : product-images/<Id of Product just saved >
            String nameUploadDir = "../product-images/"+savedProduct.getId();

            // clean files in advanced of dir :
            FileUploadUtil.cleanDirectory(nameUploadDir);

            // Then upload new file to dir :
            FileUploadUtil.uploadFileToLocalDirectory(nameUploadDir, nameUploadedFile, mainImageMultipartFile);
        }

        // in case inputExtraImageFiles have uploaded file:
        if(extraImageMultipartFiles.length > 0) {

            String nameUploadDir = "../product-images/"+savedProduct.getId()+"/extraImages";
            for(MultipartFile multipartFile : extraImageMultipartFiles) {
                // Nếu multipartFile mà empty thì add nó vào extraImages
                if(multipartFile.isEmpty())
                    continue;

                // get uploaded file from file input:
                String nameUploadedFile = StringUtils.cleanPath(multipartFile.getOriginalFilename());

                // Then upload new file to dir :
                FileUploadUtil.uploadFileToLocalDirectory(nameUploadDir, nameUploadedFile, multipartFile);
            }
        }
    }


    // UPDATE ENABLED Status :
    @GetMapping("/{id}/enabled/{enabledStatus}")
    public String updateEnabledStatus(
            @PathVariable("enabledStatus") boolean enabledStatus,
            @PathVariable("id") Integer id,
            RedirectAttributes redirectAttributes) {

        productService.updateEnabledStatus(enabledStatus, id);

        String message;
        if(enabledStatus==true)
            message = "Activate Product (id:"+id+") successfully!";
        else
            message = "Deactivate Product (id:"+id+") successfully!";
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/products";
    }

    // DELETE
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) throws ProductNotFoundException {
        try {
            productService.deleteProductById(id);
//            // after delete product, remove image dir of that product:
            String imageProductDirPath = "../product-images/"+id;
            String extraImageProductDirPath = "../product-images/"+id+"/extraImages";

            // remove extraImages Dir before, then remove product-images Dir:
            FileUploadUtil.removeDirectory(extraImageProductDirPath);
            FileUploadUtil.removeDirectory(imageProductDirPath);

            redirectAttributes.addFlashAttribute("message", "The Product(id: "+id+") has been deleted successfully");
            return "redirect:/products";

        } catch (ProductNotFoundException e) {
            throw new ProductNotFoundException(e.getMessage());
        }
    }


    // HANDLE EXCEPTION:
    @ExceptionHandler(value = ProductNotFoundException.class)
    public String handleNoSuchElementException(ProductNotFoundException productNotFoundException,RedirectAttributes redirectAttributes)
    {
        redirectAttributes.addFlashAttribute("messageError", productNotFoundException.getMessage());
        return "redirect:/products";
    }
}
