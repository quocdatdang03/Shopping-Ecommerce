package com.shoppingbackend.admin.customer.controller;

import com.shopping.common.entity.*;
import com.shopping.common.exception.ProductNotFoundException;
import com.shoppingbackend.admin.brand.exception.BrandNotFoundException;
import com.shoppingbackend.admin.category.service.CategoryServiceImpl;
import com.shoppingbackend.admin.customer.exception.CustomerNotFoundException;
import com.shoppingbackend.admin.customer.service.CustomerServiceImpl;
import com.shoppingbackend.admin.setting.country.CountryServiceImpl;
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
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerServiceImpl customerService;

    @Autowired
    private CountryServiceImpl countryService;


    @ModelAttribute("countryList")
    public List<Country> getCountryList() {
        List<Country> countryList = countryService.findAllCountry();
        return countryList;
    }

    @GetMapping("")
    public String listFirstPage(Model model) {
        List<Customer> customerList = customerService.listAllCustomer();
        model.addAttribute("customers", customerList);

        return listCustomerByPage(1,"asc","firstName",null, model);
    }

    @GetMapping("/page/{pageNumber}")
    public String listCustomerByPage(
            @PathVariable("pageNumber") Integer pageNumber,
            @Param("sortDir") String sortDir,
            @Param("sortField") String sortField,
            @Param("keyword") String keyword,
            Model model
    ) {
        if(sortDir==null || sortDir.isEmpty())
            sortDir = "asc";
        if(sortField==null || sortField.isEmpty())
            sortField = "firstName";

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        Page<Customer> pageResult = customerService.listCustomersByPage(pageNumber, sortField, sortDir, keyword);

        // get information for pagination:
        List<Customer> customerList = pageResult.getContent();
        int totalPages = pageResult.getTotalPages();
        long totalElements = pageResult.getTotalElements();
        int firstPageNumber = ((pageNumber-1)*customerService.CUSTOMER_NUMBER_PER_PAGE)+1;
        int lastPageNumber = pageNumber * customerService.CUSTOMER_NUMBER_PER_PAGE;
        if(lastPageNumber>=totalElements)
            lastPageNumber = (int) totalElements;

        model.addAttribute("customers", customerList);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalElements", totalElements);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("sortField", sortField);
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("keyword", keyword);
        model.addAttribute("firstPageNumber", firstPageNumber);
        model.addAttribute("lastPageNumber", lastPageNumber);

        return "customer/customer";
    }

    // SAVE : UPDATE:
    @PostMapping("/save")
    public String saveCustomer(
            @ModelAttribute("customer") Customer customer,
            RedirectAttributes redirectAttributes) throws IOException
    {

        customerService.saveCustomer(customer);

        redirectAttributes.addFlashAttribute("message","The Customer( name: "+customer.getFirstName()+" "+customer.getLastName()+" ) has been saved successfully!");

        return "redirect:/customers/page/1?keyword="+customer.getEmail();
    }

    // ENABLED:
    @GetMapping("/{id}/enabled/{enableStatus}")
    public String enableCustomer(
            @PathVariable("id") Integer customerId,
            @PathVariable("enableStatus") boolean enableStatus,
            RedirectAttributes redirectAttributes) throws ProductNotFoundException {
        try {
            customerService.updateEnableStatus(customerId, enableStatus);
        } catch (CustomerNotFoundException e) {
            throw new ProductNotFoundException(e.getMessage());
        }

        String message;
        if(enableStatus==true)
            message = "Activate Customer (id:"+customerId+") successfully!";
        else
            message = "Deactivate Customer (id:"+customerId+") successfully!";
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/customers";
    }

    @GetMapping("/details/{id}")
    public String showCustomerDetails(@PathVariable("id") Integer customerId, Model model) throws CustomerNotFoundException {
        try {
            Customer customer = customerService.getCustomerById(customerId);

            model.addAttribute("customer", customer);

            return "customer/customer_detail_modal";
        } catch (CustomerNotFoundException e) {
            throw new CustomerNotFoundException(e.getMessage());
        }
    }

    // SHOW EDIT FORM :
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model) throws CustomerNotFoundException {
        // get customer by id :
        Customer customer;
        try {
            customer = customerService.getCustomerById(id);

        } catch (CustomerNotFoundException e) {
            throw new CustomerNotFoundException(e.getMessage());
        }

        model.addAttribute("customer", customer);
        model.addAttribute("pageTitle", "Edit Customer (ID:"+id+")");

        return "customer/customer_form";
    }

    // DELETE
    @GetMapping("/delete/{id}")
    public String deleteByid(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) throws CustomerNotFoundException {
        // delete :
        try {
            customerService.deleteCustomerById(id);

            redirectAttributes.addFlashAttribute("message", "The Customer(id: "+id+") has been deleted successfully");
            return "redirect:/customers";
        } catch (CustomerNotFoundException e) {
            throw new CustomerNotFoundException(e.getMessage());
        }
    }

    // HANDLE EXCEPTION :
    @ExceptionHandler(value = CustomerNotFoundException.class)
    public String handleCustomerNotFoundException(CustomerNotFoundException customerNotFoundException, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("messageError", customerNotFoundException.getMessage());
        return "redirect:/customers";
    }

}
