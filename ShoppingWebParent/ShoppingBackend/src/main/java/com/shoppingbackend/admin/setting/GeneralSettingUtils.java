package com.shoppingbackend.admin.setting;

import com.shopping.common.entity.Setting;
import com.shopping.common.entity.SettingUtils;

import java.util.List;

public class GeneralSettingUtils extends SettingUtils {

    public GeneralSettingUtils(List<Setting> settingList) {
        super(settingList);
    }

    public void updateCurrencySymbol(String value) {
        super.update("CURRENCY_SYMBOL", value);
    }

    public void updateSiteLogo(String value) {
        super.update("SITE_LOGO", value);
    }
}
