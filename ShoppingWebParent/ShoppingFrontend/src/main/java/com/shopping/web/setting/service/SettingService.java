package com.shopping.web.setting.service;


import com.shopping.common.entity.Setting;
import com.shopping.web.setting.EmailSettingUtils;

import java.util.List;

public interface SettingService {
    public List<Setting> getGeneralSettings();
    public EmailSettingUtils getEmailSettings();
}
