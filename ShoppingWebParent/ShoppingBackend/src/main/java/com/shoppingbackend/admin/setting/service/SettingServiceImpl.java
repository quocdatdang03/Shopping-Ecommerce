package com.shoppingbackend.admin.setting.service;

import com.shopping.common.entity.Currency;
import com.shopping.common.entity.Setting;
import com.shopping.common.entity.SettingCategory;
import com.shoppingbackend.admin.setting.GeneralSettingUtils;
import com.shoppingbackend.admin.setting.repository.CurrencyRepository;
import com.shoppingbackend.admin.setting.repository.SettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SettingServiceImpl implements SettingService {

    @Autowired
    private SettingRepository settingRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Override
    public List<Setting> listAllSettings() {
        return (List<Setting>) settingRepository.findAll();
    }

    @Override
    public List<Currency> listAllCurrency() {
        return currencyRepository.findAllByOrderByNameAsc();
    }

    @Override
    public GeneralSettingUtils getGeneralSettingUtils() {
        List<Setting> settings = new ArrayList<Setting>();

        // General Setting includes : GENERAL and CURRENCY:
        List<Setting> generalSettings = settingRepository.findBySettingCategory(SettingCategory.GENERAL);
        List<Setting> currencySettings = settingRepository.findBySettingCategory(SettingCategory.CURRENCY);

        settings.addAll(generalSettings);
        settings.addAll(currencySettings);

        return new GeneralSettingUtils(settings);
    }

    @Override
    public void saveAll(List<Setting> settingList) {
        settingRepository.saveAll(settingList);
    }

}
