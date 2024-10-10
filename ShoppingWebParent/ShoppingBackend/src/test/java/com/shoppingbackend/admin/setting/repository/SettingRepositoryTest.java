package com.shoppingbackend.admin.setting.repository;

import com.shopping.common.entity.Setting;
import com.shopping.common.entity.SettingCategory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class SettingRepositoryTest {

    @Autowired
    private SettingRepository settingRepository;

    @Test
    public void testInsertGeneralSettings() {
        Setting setting = new Setting("SITE_NAME", "Shopping", SettingCategory.GENERAL);
        Setting setting1 = new Setting("SITE_LOGO", "Shopping.png", SettingCategory.GENERAL);
        Setting setting2 = new Setting("COPYRIGHT", "Shopping Admin - Copyright © Shopping", SettingCategory.GENERAL);

        Iterable<Setting> savedResult = settingRepository.saveAll(List.of(setting,setting1,setting2));

        Assertions.assertThat(savedResult).size().isGreaterThan(0);
    }

    @Test
    public void testInsertCurrencySettings() {
        Setting setting = new Setting("CURRENCY_ID", "1", SettingCategory.CURRENCY);
        Setting setting1 = new Setting("CURRENCY_SYMBOL", "$", SettingCategory.CURRENCY);
        Setting setting2 = new Setting("CURRENCY_SYMBOL_POSITION", "before", SettingCategory.CURRENCY);
        Setting setting3 = new Setting("DECIMAL_POINT_TYPE", "POINT", SettingCategory.CURRENCY);
        Setting setting4 = new Setting("DECIMAL_DIGITS", "2", SettingCategory.CURRENCY);
        Setting setting5 = new Setting("THOUSANDS_POINT_TYPE", "COMMA", SettingCategory.CURRENCY);

        Iterable<Setting> savedResult = settingRepository.saveAll(List.of(setting,setting1,setting2,setting3,setting4,setting5));

        Assertions.assertThat(savedResult).size().isGreaterThan(0);
    }
}
