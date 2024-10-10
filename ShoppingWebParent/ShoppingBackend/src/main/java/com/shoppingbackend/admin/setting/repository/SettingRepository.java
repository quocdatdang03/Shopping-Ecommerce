package com.shoppingbackend.admin.setting.repository;

import com.shopping.common.entity.Setting;
import com.shopping.common.entity.SettingCategory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettingRepository extends CrudRepository<Setting, String> {

    // find Setting by settingCategory:
    public List<Setting> findBySettingCategory(SettingCategory settingCategory);
}
