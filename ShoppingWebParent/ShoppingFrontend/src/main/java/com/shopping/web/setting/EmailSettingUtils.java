package com.shopping.web.setting;

import com.shopping.common.entity.Setting;
import com.shopping.common.entity.SettingUtils;

import java.util.List;

public class EmailSettingUtils extends SettingUtils {

    public EmailSettingUtils(List<Setting> settingList) {
        super(settingList);
    }

    public String getHost()
    {
        return super.getValue("MAIL_HOST");
    }

    public Integer getPort()
    {
        return Integer.valueOf(super.getValue("MAIL_PORT"));
    }

    public String getPassword()
    {
        return super.getValue("MAIL_PASSWORD");
    }

    public String getUserName()
    {
        return super.getValue("MAIL_USERNAME");
    }

    public String getSmtpAuth() {
        return super.getValue("MAIL_AUTH");
    }

    public String getSmtpSecured() {
        return super.getValue("MAIL_SECURED");
    }

    public String getFromMailAddress() {
        return super.getValue("MAIL_FROM");
    }

    public String getMailSenderName() {
        return super.getValue("MAIL_SENDER_NAME");
    }

    public String getCustomerVerifySubject() {
        return super.getValue("CUSTOMER_VERIRY_SUBJECT");
    }
    public String getCustomerVerifyContent() {
        return super.getValue("CUSTOMER_VERIRY_CONTENT");
    }

}
