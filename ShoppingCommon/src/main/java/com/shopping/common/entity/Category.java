package com.shopping.common.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", length = 128, nullable = false, unique = true)
    private String name;

    @Column(name = "alias", length = 64, nullable = false, unique = true)
    private String alias;

    @Column(name = "image", length = 128, nullable = false)
    private String image;

    @Column(name="enabled", nullable = false)
    private boolean enabled;

    @Transient
    private boolean hasChildren;

    @OneToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private Set<Category> children = new HashSet<>();

    // constructors :
    public Category(String name) {
        this.name = name;
        this.alias = name;
        this.image = "default.jpg";
        this.enabled = false;
    }

    public Category(String name, Category parentCategory) {
        this(name);
        this.parent = parentCategory;
    }

    public static Category copyFull(Category category) {
        Category copyCategory = new Category();
        copyCategory.setId(category.getId());
        copyCategory.setName(category.getName());
        copyCategory.setAlias(category.getAlias());
        copyCategory.setImage(category.getImage());
        copyCategory.setEnabled(category.isEnabled());

        copyCategory.setHasChildren(category.getChildren().size()>0);

        return copyCategory;
    }

    public static Category copyFull(Category category, String name) {
        Category copyCategory = Category.copyFull(category);
        copyCategory.setName(name);

        return copyCategory;
    }


    // Đánh dấu @Transient để không liên kết nó với bất kì column nào trong DB
    @Transient
    public String getImagePath()
    {
        if(this.getId()==null || this.getImage().isEmpty())
            return "/images/img-thumbnail-default.jpg";
        else
            return "/category-images/"+this.getId()+"/"+this.getImage();
    }

    public boolean isHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }
}
