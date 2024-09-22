package com.shopping.common.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="products")
public class Product {
    @Id
    @GeneratedValue
    private Integer id;

    // 1 category can have many products, 1 product belongs to only 1 category
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    // 1 brand can have many products, 1 product belongs to only 1 brand
    @ManyToOne
    @JoinColumn(name="brand_id")
    private Brand brand;

    @Column(name="name", length = 256, nullable = false, unique = true)
    private String name;

    @Column(name="alias", length = 256, nullable = false, unique = true)
    private String alias;

    @Column(name="short_description", length = 1024, nullable = false)
    private String shortDescription;

    @Column(name="full_description", length = 4096, nullable = false)
    private String fullDescription;

//    @Column(name="main_image", length = 45)
//    private String mainImage;

    @Column(name="created_time")
    private Date createdTime;

    @Column(name="updated_time")
    private Date updatedTime;

    @Column(name="enabled", nullable = false)
    private boolean enabled;

    @Column(name="in_stock")
    private boolean inStock;

    @Column(name="price", nullable = false)
    private float price;

    @Column(name="cost", nullable = false)
    private float cost;

    @Column(name="discount_percent")
    private float discountPercent;

    @Column(name="length", nullable = false)
    private float length;

    @Column(name="width", nullable = false)
    private float width;

    @Column(name="height", nullable = false)
    private float height;

    @Column(name="weight", nullable = false)
    private float weight;

}
