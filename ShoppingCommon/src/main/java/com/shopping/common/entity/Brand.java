package com.shopping.common.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name="brands")
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 45, nullable = false, unique = true)
    private String name;

    @Column(length = 45, nullable = false)
    private String logo;

    // 1 Brand can have many categories
    @ManyToMany
    @JoinTable(
            name = "brands_categories",
            joinColumns = {@JoinColumn(name = "brand_id")},
            inverseJoinColumns = {@JoinColumn(name = "category_id")}
    )
    private Set<Category> categories = new HashSet<Category>();

    public Brand(String name, Category category) {
        this.name = name;
        this.logo = "brand-logo.png";

        categories.add(category);
    }

    public Brand(String name, Set<Category> categorySet) {
        this.name = name;
        this.logo = "brand-logo.png";

        categories.addAll(categorySet);
    }

    @Transient
    public String getImagePath() {
        if(this.getId() == null || this.getLogo().isEmpty())
            return "/images/img-thumbnail-default.jpg";
        else
            return "/brand-images/"+this.getId()+"/"+this.getLogo();
    }


}
