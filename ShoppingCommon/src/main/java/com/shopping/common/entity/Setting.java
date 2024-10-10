package com.shopping.common.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name="settings")
public class Setting {

    @Id
    // Dùng dấu `` vì key là keyword trong mysql do đó nếu muốn column name là key thì phải thêm dấu ``
    @Column(name="`key`", length = 128)
    private String key;

    @Column(name="value", length = 1024)
    private String value;

    // @Enumerated : dùng để chỉ định Enum name chính là value cho cột này:
    @Enumerated(EnumType.STRING)
    @Column(length = 45, nullable = false)
    private SettingCategory settingCategory;


    public Setting(String key) {
        this.key = key;
    }

    // phải có equals() và hashCode() là "key" để phân biệt giữa các Setting obj với nhau:
    // Nhằm dùng cho method indexOf() của List :
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Setting setting = (Setting) o;
        return key.equals(setting.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
