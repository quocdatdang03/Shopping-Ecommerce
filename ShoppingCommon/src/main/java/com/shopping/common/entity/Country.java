package com.shopping.common.entity;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name="countries")
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="name", length = 64, nullable = false)
    private String name;

    @Column(name="code", length = 10, nullable = false)
    private String code;

    @Getter(AccessLevel.NONE) // don't generate getter and setter for field states
    @Setter(AccessLevel.NONE)
    @ToString.Exclude
    @OneToMany(mappedBy = "country")
    private Set<State> states = new HashSet<State>();

    public Country(Integer id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    public Country(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public Country(Integer id) {
        this.id = id;
    }

}
