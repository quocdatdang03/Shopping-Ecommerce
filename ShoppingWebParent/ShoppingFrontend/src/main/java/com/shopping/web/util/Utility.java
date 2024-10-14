package com.shopping.web.util;

import com.shopping.web.setting.EmailSettingUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.servlet.http.HttpServletRequest;
import java.util.Properties;

public class Utility {
    public static String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        // siteURL nó trả về full actual url --> ta chỉ muốn lấy url excluding servletPath: localhost:9090/shopping without /... ở sau
        return siteURL.replace(request.getServletPath(), "");
    }

    public static JavaMailSender setJavaMailSender(EmailSettingUtils settingUtils) {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(settingUtils.getHost());
        javaMailSender.setPort(settingUtils.getPort());
        javaMailSender.setUsername(settingUtils.getUserName());
        javaMailSender.setPassword(settingUtils.getPassword());

        Properties mailProperties = new Properties();
        mailProperties.setProperty("mail.smtp.auth", settingUtils.getSmtpAuth());
        mailProperties.setProperty("mail.smtp.starttls.enable", settingUtils.getSmtpSecured());

        javaMailSender.setJavaMailProperties(mailProperties);

        return javaMailSender;
    }
}
