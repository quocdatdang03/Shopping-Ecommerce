package com.shopping.common.entity;

import com.shopping.common.entity.Country;
import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name="states")
public class State {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="name", length = 64, nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    public State(String name, Country country) {
        this.name = name;
        this.country = country;
    }
}
