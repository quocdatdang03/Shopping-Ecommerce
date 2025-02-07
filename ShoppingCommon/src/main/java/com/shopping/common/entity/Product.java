package com.shopping.common.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@Entity
@Table(name="products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 1 category can have many products, 1 product belongs to only 1 category
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    // 1 brand can have many products, 1 product belongs to only 1 brand
    @ManyToOne
    @JoinColumn(name="brand_id")
    private Brand brand;

    // 1 product can have many productImage, 1 productImage belongs to only 1 product
    /*
       + So sánh CascadeType.REMOVE và orphanRemoval :
            - REMOVE :
                + entity cha bị xóa -> entity con của entity cha đó cũng bị xóa theo
                + Nhưng chỉ loại bỏ 1 entity con ra khỏi Collection mà không xóa entity cha (lúc này entity con trở thành orphan) -> entity con đó vẫn không bị xóa khỏi DB
            - orphanRemoval :
                + Ngoài việc xóa các thực thể con khi thực thể cha bị xóa, nó còn xóa các thực thể con khi chúng bị loại bỏ khỏi tập hợp,
                        bất kể trạng thái của thực thể cha.
    * */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductImage> extraImages = new HashSet<ProductImage>();

    // 1 product can have many productDetail, 1 productDetail belongs to only 1 product
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductDetail> details = new HashSet<ProductDetail>();

    @Column(name="name", length = 256, nullable = false, unique = true)
    private String name;

    @Column(name="alias", length = 256, nullable = false, unique = true)
    private String alias;

    @Column(name="short_description", length = 1024, nullable = false)
    private String shortDescription;

    @Column(name="full_description", length = 4096, nullable = false)
    private String fullDescription;

    @Column(name="main_image", length = 45, nullable = false)
    private String mainImage;

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

    @Transient
    public String getMainImagePath() {
        if(this.getId()==null || this.getMainImage().isEmpty()) {
            return "/images/img-thumbnail-default.jpg";
        }
        else {
            return "/product-images/"+this.getId()+"/"+this.getMainImage();
        }
    }

    public void addExtraImages(String nameExtraImage) {
        this.getExtraImages().add(new ProductImage(nameExtraImage, this));
    }

    public void addProductDetail(String name, String value)
    {
        this.getDetails().add(new ProductDetail(name, value, this));
    }

    public void addProductDetail(Integer id, String name, String value)
    {
        this.getDetails().add(new ProductDetail(id, name, value, this));
    }



    public boolean containsExtraImage(String imageFileName) {
        for(ProductImage item : this.getExtraImages()) {
            if(item.getName().equals(imageFileName))
                return true;
        }
        return false;
    }


    // get Discount Price :
    @Transient
    public float getDiscountPrice() {
        float discountPrice;
        if(this.getDiscountPercent()>0)
        {
            discountPrice = this.getPrice() * ((100-this.getDiscountPercent())/100);
//            // làm tròn 2 số thập phân
//            return (float) (Math.round(discountPrice * 100.0) / 100.0);
            return discountPrice;
        }
        else {
            return this.getPrice();
        }
    }

    // get short name:
    @Transient
    public String getShortName() {
        if(name.length()>50)
            return name.substring(0,50).concat("...");
        return name;
    }
}
