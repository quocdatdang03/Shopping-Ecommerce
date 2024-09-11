package com.shopping.common.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="email",nullable = false, unique = true, length = 128)
//    @NotBlank(message = "Email must not be empty!")
    private String email;

    @Column(name="first_name", nullable = false, length = 45)
    private String firstName;

    @Column(name="last_name", nullable = false, length = 45)
    private String lastName;

    @Column(name="password", nullable = false, length = 64)
//    @NotBlank(message = "Password must not be empty!")
//    @Size(min = 5, message = "Password must be at least {min} characters long")
    private String password;

    @Column(name="photos", nullable = true, length = 64)
    private String photos;

    @Column(name="enabled",nullable = false)
    private boolean enabled;

    @ManyToMany
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name="role_id")
    )
    private Set<Role> roles = new HashSet<>();

    // demo test:
    public void addRole(Role role)
    {
        this.roles.add(role);
    }

}
