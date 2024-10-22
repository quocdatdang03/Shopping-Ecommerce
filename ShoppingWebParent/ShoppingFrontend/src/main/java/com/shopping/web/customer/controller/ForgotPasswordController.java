package com.shopping.web.customer.controller;

import com.shopping.common.entity.Customer;
import com.shopping.common.exception.CustomerNotFoundException;
import com.shopping.web.customer.service.CustomerServiceImpl;
import com.shopping.web.setting.EmailSettingUtils;
import com.shopping.web.setting.service.SettingServiceImpl;
import com.shopping.web.util.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

@Controller
public class ForgotPasswordController {

    @Autowired
    private CustomerServiceImpl customerService;

    @Autowired
    private SettingServiceImpl settingService;

    @GetMapping("/forgot_password")
    public String showForgotPasswordRequestForm() {
        return "/customer/forgot_password_form";
    }

    @PostMapping("/forgot_password")
    public String processForgotPasswordRequestForm(HttpServletRequest request) throws CustomerNotFoundException, UnsupportedEncodingException, MessagingException {

        //get email from Form:
        String email = request.getParameter("email");

        // update resetPasswordToken
        try {
            customerService.updateResetPasswordToken(email);
        } catch (CustomerNotFoundException e) {
            throw new CustomerNotFoundException(e.getMessage());
        }

        sendResetPasswordEmail(request, email);

        return "/customer/forgot_password_inform";
    }

    private void sendResetPasswordEmail(HttpServletRequest request, String email) throws MessagingException, UnsupportedEncodingException {

        Customer customer = customerService.getCustomerByEmail(email);

        EmailSettingUtils emailSettingUtils = settingService.getEmailSettings();
        JavaMailSender javaMailSender = Utility.setJavaMailSender(emailSettingUtils);

        String toCustomerEmailAddress = email;
        String subject = "Reset " +
                "Your Password";
        String content = " <div>\n" +
                "        <h1 style=\"text-align: center; color: #f95c04;\">[[mailSenderName]]</h1>\n" +
                "        <h2 style=\"text-align: center; margin: 20px 0 20px 0;\">Hi [[userName]], let's reset your password</h2>\n" +
                "        <div style=\"text-align: center;\">\n" +
                "            <a style=\"background-color: #f95c04; padding: 8px 15px; text-decoration: none; color: white; font-weight: 600; border-radius: 2px;\"\n" +
                "                href=\"[[resetPasswordLink]]\">\n" +
                "                Reset your password\n" +
                "            </a>\n" +
                "        </div>\n" +
                "    </div>";

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        mimeMessageHelper.setFrom(emailSettingUtils.getFromMailAddress(), emailSettingUtils.getMailSenderName());
        mimeMessageHelper.setTo(toCustomerEmailAddress);
        mimeMessageHelper.setSubject(subject);

        content = content.replace("[[userName]]", customer.getFirstName()+" "+customer.getLastName());
        content = content.replace("[[mailSenderName]]", emailSettingUtils.getMailSenderName());
        String resetPasswordURL = Utility.getSiteURL(request)+"/reset_password?token="+customer.getResetPasswordToken();
        content = content.replace("[[resetPasswordLink]]", resetPasswordURL);

        // setText(content, isHtml(boolean) )
        mimeMessageHelper.setText(content,true);

        javaMailSender.send(mimeMessage);
    }


    @GetMapping("/reset_password")
    public String showResetPasswordForm(@RequestParam("token") String resetPasswordToken, Model model) throws CustomerNotFoundException {
        System.out.println("TOKEN: "+resetPasswordToken);
        Customer customer = customerService.getCustomerByResetPasswordToken(resetPasswordToken);
        if(customer==null)
        {
            throw new CustomerNotFoundException("Invalid Token");
        }

        model.addAttribute("resetPasswordToken", resetPasswordToken);

        return "customer/reset_password_form";

    }

    @PostMapping("/reset_password")
    public String resetPassword(
            @RequestParam("newPassword") String newPassword,
            @RequestParam("resetPasswordToken") String resetPasswordToken,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        try {
            // reset password:
            customerService.resetPassword(newPassword, resetPasswordToken);
        } catch (CustomerNotFoundException customerNotFoundException) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not find any Customer");
            return "redirect:/reset_password_form";
        }

        model.addAttribute("pageTitle", "Change Password successfully");
        return "customer/reset_password_success";
    }


    @ExceptionHandler(value = CustomerNotFoundException.class)
    public String handleCustomerNotFoundException(CustomerNotFoundException customerNotFoundException, RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("errorMessage", customerNotFoundException.getMessage());

        return "redirect:/forgot_password";
    }
}
