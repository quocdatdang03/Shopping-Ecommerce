package com.shopping.web.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

// This class will create Spring Security Filter Chain
@EnableWebSecurity // @EnableWebSecurity will help me to create bean named ‘springSecurityFilterChain’
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().permitAll();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // ignore authentication với các folder dưới:
        web.ignoring().antMatchers("/images/**", "/js/**", "/css/**", "/webjars/**");
    }
}
