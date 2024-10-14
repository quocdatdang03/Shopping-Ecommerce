package com.shopping.common.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(of = {"id","email", "firstName", "lastName", "phoneNumber"})
@Entity
@Table(name="customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name="password", nullable = false, length = 64)
    private String password;


    @Column(name="first_name", nullable = false, length = 45)
    private String firstName;

    @Column(name="last_name", nullable = false, length = 45)
    private String lastName;

    @Column(name="phone_number", nullable = false, length = 45)
    private String phoneNumber;

    @Column(name="address_line_1", nullable = false, length = 45)
    private String addressLine1;

    @Column(name="address_line_2", length = 45)
    private String addressLine2;

    @Column(name="city", nullable=false, length = 45)
    private String city;

    @Column(name="state", nullable=false, length = 45)
    private String state;

    @Column(name="postal_code", nullable=false, length = 10)
    private String postalCode;

    @Column(name="verification_code", length = 64)
    private String verificationCode;

    @Column(name="enabled")
    private boolean enabled;

    @Column(name="created_time")
    private Date createdTime;

    @ManyToOne
    @JoinColumn(name="country_id")
    private Country country;

    @Transient
    public String getFullName() {
        return this.getFirstName()+" "+this.getLastName();
    }

}
