package com.shoppingbackend.admin.security;

import com.shopping.common.entity.Role;
import com.shopping.common.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

// Custom implementation class of interface UserDetails
public class ShoppingUserDetails implements UserDetails {
    private User user;

    public ShoppingUserDetails(User user)
    {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // list roles of user:
        Set<Role> roles = user.getRoles();
        List<SimpleGrantedAuthority> userRoles = new ArrayList<SimpleGrantedAuthority>();
        for(Role role : roles)
        {
            userRoles.add(new SimpleGrantedAuthority(role.getName()));
        }
        return userRoles;
    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        // username mà ta sử dụng là field email:
        return this.user.getEmail();
    }

    // Cho mặc định return true:
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Cho mặc định return true:
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // Cho mặc định return true:
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }
}
