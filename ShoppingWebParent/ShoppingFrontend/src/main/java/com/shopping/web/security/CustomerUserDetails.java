package com.shopping.web.security;

import com.shopping.common.entity.Customer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomerUserDetails implements UserDetails {
    private Customer customer;

    public CustomerUserDetails(Customer customer) {
        this.customer = customer;
    }

    // Customer : doesn't have role -> return null;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.customer.getPassword();
    }

    @Override
    public String getUsername() {
        return this.customer.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.customer.isEnabled();
    }

    public String getFullName() {
        return this.customer.getFullName();
    }

    public Customer getCustomer() {
        return this.customer;
    }
}
