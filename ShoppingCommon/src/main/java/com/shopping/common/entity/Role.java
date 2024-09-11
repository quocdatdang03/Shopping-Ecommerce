package com.shopping.common.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;


@Getter
@Setter
/*Trong phương thức toString() của lớp Role, bạn in ra trường users, là một tập hợp các đối tượng User.
Nhưng trong lớp User, phương thức toString() lại in ra trường roles, là một tập hợp các đối tượng Role.
Khi toString() của User được gọi, nó lại tiếp tục gọi toString() của Role và lặp đi lặp lại không ngừng, dẫn đến lỗi StackOverflowError*/
@RequiredArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NonNull
    @Column(name="name", nullable = false, unique = true, columnDefinition = "NVARCHAR(40)")
    private String name;

    @NonNull
    @Column(name="description", nullable = false, columnDefinition = "NVARCHAR(150)")
    private String description;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    @Override
    public String toString() {
        return this.name;
    }
}
