package com.shopping.common.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="product_images")
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name="product_id")
    private Product product;

    public ProductImage(String name, Product product) {
        this.name = name;
        this.product = product;
    }


}
