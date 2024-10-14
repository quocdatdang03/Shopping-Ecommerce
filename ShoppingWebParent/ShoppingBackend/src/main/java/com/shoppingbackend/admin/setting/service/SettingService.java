package com.shoppingbackend.admin.setting.service;

import com.shopping.common.entity.Currency;
import com.shopping.common.entity.Setting;
import com.shoppingbackend.admin.setting.GeneralSettingUtils;

import java.util.List;

public interface SettingService {
    public List<Setting> listAllSettings();
    public List<Currency> listAllCurrency();
    public GeneralSettingUtils getGeneralSettingUtils();
    public List<Setting> listMailServerSetting();
    public List<Setting> listMailTemplateSettings();
    public void saveAll(List<Setting> settingList);
}
