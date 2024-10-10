package com.shopping.common.entity;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="currencies")
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="name", length = 64, nullable = false)
    private String name;

    @Column(name="symbol", length = 5, nullable = false)
    private String symbol;

    @Column(name="code", length = 5, nullable = false)
    private String code;

    public Currency(String name, String symbol, String code) {
        this.name = name;
        this.symbol = symbol;
        this.code = code;
    }

    @Override
    public String toString() {
       return name +" - "+ symbol +" - "+code;
    }
}
