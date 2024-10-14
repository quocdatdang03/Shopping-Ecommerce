package com.shopping.web.setting.service;


import com.shopping.common.entity.Setting;
import com.shopping.common.entity.SettingCategory;
import com.shopping.web.setting.EmailSettingUtils;
import com.shopping.web.setting.repository.SettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettingServiceImpl implements SettingService {

    @Autowired
    private SettingRepository settingRepository;

    @Override
    public List<Setting> getGeneralSettings() {
        // General Setting include 2 settingCategories : GENERAL and CURRENCY
        return settingRepository.findSettingByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);
    }

    @Override
    public EmailSettingUtils getEmailSettings() {
        List<Setting> settings = settingRepository.findSettingByTwoCategories(SettingCategory.MAIL_SERVER, SettingCategory.MAIL_TEMPLATE);

        return new EmailSettingUtils(settings);
    }
}
