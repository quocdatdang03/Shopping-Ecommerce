package com.shopping.web.product.controller;

import com.shopping.common.entity.Category;
import com.shopping.common.entity.Product;
import com.shopping.common.exception.CategoryNotFoundException;
import com.shopping.common.exception.ProductNotFoundException;
import com.shopping.web.category.service.CategoryServiceImpl;
import com.shopping.web.product.service.ProductServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private CategoryServiceImpl categoryService;

    @GetMapping("/c/{category_alias}")
    public String viewProductByCategoryFirstPage(@PathVariable("category_alias") String categoryAlias, Model model) {
        Integer pageNumber = 1;
        String keyword = null;
        return viewProductByCategoryByPage(categoryAlias, pageNumber, keyword, model);
    }

    @GetMapping("/c/{category_alias}/page/{pageNumber}")
    public String viewProductByCategoryByPage(
            @PathVariable("category_alias") String categoryAlias,
            @PathVariable("pageNumber") Integer pageNumber,
            @Param("keyword") String keyword,
            Model model) {
        try {
            Category category = categoryService.getCategoryByAlias(categoryAlias);
            if(category==null)
                return "error/404";

            List<Category> categoryParentList = categoryService.listCategoryParents(category);

            Page<Product> pageResult = productService.listProductByCategory(pageNumber, category, keyword);
            List<Product> productList = pageResult.getContent();
            int totalPages = pageResult.getTotalPages();
            long totalElements = pageResult.getTotalElements();
            int firstPageNumber = ((pageNumber-1)*productService.NUMBER_PRODUCT_PER_PAGE)+1;
            int lastPageNumber = pageNumber*productService.NUMBER_PRODUCT_PER_PAGE;
            if(lastPageNumber>=totalElements)
                lastPageNumber = (int) totalElements;


            // information for bread crumb :
            model.addAttribute("categoryParentList", categoryParentList);
            model.addAttribute("categoryAlias", categoryAlias);

            // information for sub category:
            model.addAttribute("category", category);

            // information for paginating:
            model.addAttribute("productList", productList);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalElements", totalElements);
            model.addAttribute("firstPageNumber", firstPageNumber);
            model.addAttribute("lastPageNumber", lastPageNumber);
            model.addAttribute("pageNumber", pageNumber);

            model.addAttribute("keyword", keyword);

            model.addAttribute("pageTitle", category.getName());

            return "product/products_by_category";
        }
        catch(CategoryNotFoundException e)
        {
            return "error/404";
        }
    }

    @GetMapping("/p/{product_alias}")
    public String showProductDetail(@PathVariable("product_alias") String alias, Model model)
    {
        try {
            Product product = productService.getProductByAlias(alias);
            Category category = categoryService.getCategoryByAlias(product.getCategory().getAlias());
            List<Category> categoryParentList = categoryService.listCategoryParents(category);

            model.addAttribute("product", product);
            model.addAttribute("categoryParentList",categoryParentList);

            model.addAttribute("pageTitle", product.getName());

        } catch (ProductNotFoundException | CategoryNotFoundException e) {
            return "error/404";
        }
        return "product/product_details";
    }
}
