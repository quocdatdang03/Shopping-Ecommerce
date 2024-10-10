package com.shopping.web.setting.repository;

import com.shopping.common.entity.Setting;
import com.shopping.common.entity.SettingCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettingRepository extends CrudRepository<Setting, String> {

    // find Setting by settingCategory:
    public List<Setting> findBySettingCategory(SettingCategory settingCategory);

    // find by 2 categories :
    @Query("SELECT s FROM Setting s WHERE s.settingCategory IN (?1,?2)")
    public List<Setting> findSettingByTwoCategories(SettingCategory category1, SettingCategory category2);
}
