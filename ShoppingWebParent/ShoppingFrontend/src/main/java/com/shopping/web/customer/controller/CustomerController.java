package com.shopping.web.customer.controller;

import com.shopping.common.entity.Country;
import com.shopping.common.entity.Customer;
import com.shopping.web.customer.service.CustomerServiceImpl;
import com.shopping.web.setting.EmailSettingUtils;
import com.shopping.web.setting.service.SettingServiceImpl;
import com.shopping.web.util.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Controller

public class CustomerController {

    @Autowired
    private CustomerServiceImpl customerService;

    @Autowired
    private SettingServiceImpl settingService;

    @GetMapping("/register")
    public String showFormRegister(@ModelAttribute("customer") Customer customer, Model model) {
        List<Country> countryList = customerService.findAllCountry();

        model.addAttribute("countryList", countryList);
        model.addAttribute("pageTitle", "Customer Registration");

        return "customer/register_form";
    }

    @PostMapping("/register")
    public String registerCustomerAccount(
            @ModelAttribute("customer") Customer customer,
            Model model,
            HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {

        customerService.registerCustomer(customer);

        // send email verification:
        sendVerificationEmail(request, customer);

        model.addAttribute("pageTitle", "Register Account Successfully");

        return "/customer/register_verify";
    }

    private void sendVerificationEmail(HttpServletRequest request, Customer customer) throws MessagingException, UnsupportedEncodingException {
        EmailSettingUtils emailSettingUtils = settingService.getEmailSettings();
        JavaMailSender javaMailSender = Utility.setJavaMailSender(emailSettingUtils);

        String toCustomerEmailAddress = customer.getEmail();
        String subject = emailSettingUtils.getCustomerVerifySubject();
        String content = emailSettingUtils.getCustomerVerifyContent();

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

        mimeMessageHelper.setFrom(emailSettingUtils.getFromMailAddress(), emailSettingUtils.getMailSenderName());
        mimeMessageHelper.setTo(toCustomerEmailAddress);
        mimeMessageHelper.setSubject(subject);

        content = content.replace("[[userName]]", customer.getFirstName()+" "+customer.getLastName());
        String verificationURL = Utility.getSiteURL(request)+"/verifyAccount?code="+customer.getVerificationCode();
        content = content.replace("[[verificationLink]]", verificationURL);

        // setText(content, isHtml(boolean) )
        mimeMessageHelper.setText(content,true);

        System.out.println("Email of Customer: "+toCustomerEmailAddress);
        System.out.println("Verification URL: "+verificationURL);

        javaMailSender.send(mimeMessage);
    }
    
    @GetMapping("/verifyAccount")
    public String verifyAccount(@RequestParam(value = "code", required = false) String code, Model model) {
        System.out.println("Code: "+code);
        boolean isVerified = customerService.verifyCustomerAccount(code);

        if(isVerified)
        {
            model.addAttribute("pageTitle", "Verification Succeed!");
            return "customer/register_success";
        }
        else {
            model.addAttribute("pageTitle", "Verification Failed!");
            return "customer/register_fail";
        }
    }

}
