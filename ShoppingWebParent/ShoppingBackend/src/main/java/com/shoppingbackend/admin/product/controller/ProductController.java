package com.shoppingbackend.admin.product.controller;

import com.shopping.common.entity.*;
import com.shopping.common.exception.ProductNotFoundException;
import com.shoppingbackend.admin.brand.service.BrandService;
import com.shoppingbackend.admin.brand.service.BrandServiceImpl;
import com.shoppingbackend.admin.category.service.CategoryServiceImpl;
import com.shoppingbackend.admin.product.service.ProductServiceImpl;
import com.shoppingbackend.admin.security.ShoppingUserDetails;
import com.shoppingbackend.admin.util.FileUploadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/products")
public class ProductController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

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
        Integer pageNumber = 1;
        String sortField = "name";
        String sortDir = "asc";
        String keyword = null;
        Integer categoryId = 0; // all categories
        return listProductsBypage(1, sortField, sortDir, keyword, categoryId, model);
    }

    @GetMapping("/page/{pageNumber}")
    public String listProductsBypage(
            @PathVariable("pageNumber") Integer pageNumber,
            @Param("sortField") String sortField,
            @Param("sortDir") String sortDir,
            @Param("keyword") String keyword,
            @Param("categoryId") Integer categoryId,
            Model model
    )
    {
        if(sortDir==null || sortDir.isEmpty())
            sortDir = "asc";

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        Page<Product> page = productService.listProductByPage(pageNumber, sortField, sortDir, keyword,categoryId);

        // get List Categories In Form :
        List<Category> categoryList = categoryService.listCategoryInForm();

        // get information from page:
        List<Product> products = page.getContent();
        int totalPages = page.getTotalPages();
        long totalElements = page.getTotalElements();
        int firstPageNumber = ((pageNumber-1)*productService.NUMBER_PRODUCT_PER_PAGE)+1;
        int lastPageNumber = pageNumber*productService.NUMBER_PRODUCT_PER_PAGE;
        if(lastPageNumber>=totalElements)
            lastPageNumber = (int) totalElements;

        model.addAttribute("products", products);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalElements", totalElements);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("sortField", sortField);
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("keyword", keyword);
        model.addAttribute("firstPageNumber", firstPageNumber);
        model.addAttribute("lastPageNumber", lastPageNumber);
        model.addAttribute("categoryList", categoryList);

        if(categoryId!=null)
            model.addAttribute("selectedCategoryId", categoryId);

        return "product/product";
    }

    @GetMapping("/new")
    public String showProductForm(@ModelAttribute("product") Product product, Model model) {
        // by default set enabled and inStock are true :
        product.setEnabled(true);
        product.setInStock(true);

        model.addAttribute("pageTitle", "Create New Product");

        // Create mode -> extra image is 0
        model.addAttribute("numberOfExistingExtraImages", 0);
        model.addAttribute("numberOfExistingDetails", 0);

        return "product/product_form";
    }

    // SAVE : CREATE OR UPDATE :
    @PostMapping("/save")
    public String saveProduct(
            @ModelAttribute("product") Product product,
            @RequestParam(value = "inputImageFile", required = false) MultipartFile mainImageMultipartFile,
            @RequestParam(value = "inputExtraImageFile", required = false) MultipartFile[] extraImagesMultipartFiles,
            @RequestParam(value = "detailName", required = false) String[] detailNames,
            @RequestParam(value = "detailValue", required = false) String[] detailValues,
            @RequestParam(value = "imageIds", required = false) String[] extraImageIds,
            @RequestParam(value = "imageNames", required = false) String[] extraImageNames,
            @RequestParam(value = "detailIds", required = false) String[] detailIds,
            @AuthenticationPrincipal ShoppingUserDetails shoppingUserDetails,
            RedirectAttributes redirectAttributes) throws IOException, ProductNotFoundException {

        // handle save by role Salesperson (Update only price, cost, discount percent for Product):
        // Nếu mà user (đã authentication, tức đã Login) mà có role là Salesperson thì thực hiện code này:
        if(shoppingUserDetails.hasRole("Salesperson"))
        {
            try {
                productService.saveProductPrice(product);
                redirectAttributes.addFlashAttribute("message","The Product( name: "+product.getName()+" ) has been saved successfully!");

                return "redirect:/products";
            } catch (ProductNotFoundException e) {
                throw new ProductNotFoundException(e.getMessage());
            }
        }


        // ---------- 1. handle product images:
        // set main image Product
        setMainImageName(mainImageMultipartFile, product);

        // Then, set existing extra images for Product :
        setExistingExtraImages(extraImageIds, extraImageNames, product);

        // Then, set new extra images :
        setNewExtraImageNames(extraImagesMultipartFiles, product);

        // Delete các extra images trong Local Dir mà không còn tồn tại trong Form (hay DB) nữa:
        deleteExtraImagesWereRemovedOnForm(product);


        // ---------- 2. handle product details:
        // set product detail (name, value) for Product:
        setProductDetails(detailIds,detailNames, detailValues, product);

        // save product to DB:
        Product savedProduct = productService.saveProduct(product);


        saveUploadedImage(mainImageMultipartFile, extraImagesMultipartFiles, savedProduct);

        redirectAttributes.addFlashAttribute("message","The Product( name: "+product.getName()+" ) has been saved successfully!");

        return "redirect:/products";
    }


    // ------------------ Method for create and edit product images and product details
    // ------- for product images :
    private void setMainImageName(MultipartFile multipartFile, Product product) {
        // in case file input has uploaded file:
        if(multipartFile!=null && !multipartFile.isEmpty()) {
            // get uploaded file from file input:
            String nameUploadedFile = StringUtils.cleanPath(multipartFile.getOriginalFilename());

            product.setMainImage(nameUploadedFile);
        }
    }

    private void setExistingExtraImages(String[] extraImageIds, String[] extraImageNames, Product product) {
        // Nếu mà extraImagesIds hay extraImageNames không có giá trị thì thoát hàm không làm gì cả
        if(extraImageIds==null || extraImageIds.length==0)
            return;

        Set<ProductImage> productImageSet = new HashSet<ProductImage>();

        // get existing extra images from Form
        for(int i=0; i<extraImageIds.length; i++) {
            Integer extraImageId = Integer.parseInt(extraImageIds[i]);
            String extraImageName = extraImageNames[i];

            productImageSet.add(new ProductImage(extraImageId, extraImageName, product));
        }

        // set existing extra images
        product.setExtraImages(productImageSet);
    }

    private void setNewExtraImageNames(MultipartFile[] multipartFiles, Product product) {
        // Nếu mà có thêm file new extraImage thì set, không thì không làm gì cả
        if(multipartFiles!=null && multipartFiles.length > 0)
        {
            for(MultipartFile multipartFile : multipartFiles) {
                // Nếu multipartFile mà empty thì add nó vào extraImages
                if(multipartFile.isEmpty())
                    continue;
                // get uploaded file from file input:
                String nameUploadedFile = StringUtils.cleanPath(multipartFile.getOriginalFilename());

                // check if this image name doesn't exist in DB -> add new extra image:
                if(!product.containsExtraImage(nameUploadedFile))
                    product.addExtraImages(nameUploadedFile);
            }
        }
    }

    private void deleteExtraImagesWereRemovedOnForm(Product product) {
        String extraImageDir = "../product-images/"+product.getId()+"/extraImages";
        Path extraImageDirPath = Paths.get(extraImageDir);
        try {
            // Lặp qua các file trong extraImageDir -> check nếu extra images của Product không chứa file nào trong extraImageDir thì xóa file đó khỏi đó
            Files.list(extraImageDirPath).forEach(fileItem -> {
                String fileName = fileItem.getFileName().toString();
                if(!product.containsExtraImage(fileName)) {
                    try {
                        Files.delete(fileItem);
                    } catch (IOException e) {
                        LOGGER.error("Could not delete extra image file: "+fileName);
                    }
                }
            });
        }
        catch(IOException e)
        {
            LOGGER.error("Could not list extra image directory: "+extraImageDir);
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

    // ------- for product details :


    private void setProductDetails(String[] detailIds, String[] detailNames, String[] detailValues, Product product) {
        // Nếu detail không có giá trị thì không làm gì cả :
        if(detailNames==null || detailNames.length==0)
            return;

        if(detailValues==null || detailValues.length==0)
            return;

        for(int i=0; i < detailNames.length; i++)
        {
            String name = detailNames[i];
            String value = detailValues[i];
            Integer id =  Integer.parseInt((detailIds[i]));

            if(id!=0)
            {
                // In case change existing detail one:
                product.addProductDetail(id, name, value);

            }
            else if(!name.isEmpty() && !value.isEmpty())
            {
                // In case add new detail one
                // if name and value is not empty -> add product detail
                product.addProductDetail(name, value);
            }
        }

    }



    // ------------------ END Method for create and edit product images and product details


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
            String extraImageProductDirPath = "../product-images/"+id+"/extras";

            // remove extras Dir before, then remove product-images Dir:
            FileUploadUtil.removeDirectory(extraImageProductDirPath);
            FileUploadUtil.removeDirectory(imageProductDirPath);

            redirectAttributes.addFlashAttribute("message", "The Product(id: "+id+") has been deleted successfully");
            return "redirect:/products";

        } catch (ProductNotFoundException e) {
            throw new ProductNotFoundException(e.getMessage());
        }
    }

    // SHOW EDIT :
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model) throws ProductNotFoundException {
        try {
            Product product = productService.getProductById(id);
            model.addAttribute("product", product);
            model.addAttribute("pageTitle", "Edit Product (ID:"+id+")");

            // Edit mode -> extra images are
            model.addAttribute("numberOfExistingExtraImages", product.getExtraImages().size());
            model.addAttribute("numberOfExistingDetails", product.getDetails().size());


            return "/product/product_form";
        } catch (ProductNotFoundException e) {
            throw new ProductNotFoundException(e.getMessage());
        }
    }

    // VIEW PRODUCT DETAIL:
    @GetMapping("/details/{id}")
    public String showProductDetails(@PathVariable("id") Integer id, Model model) throws ProductNotFoundException {
        try {
            Product product = productService.getProductById(id);

            model.addAttribute("product", product);

            return "/product/product_detail_modal";

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
