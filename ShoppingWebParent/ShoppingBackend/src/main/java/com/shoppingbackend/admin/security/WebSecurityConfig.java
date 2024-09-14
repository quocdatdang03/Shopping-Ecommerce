package com.shoppingbackend.admin.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// This class will create Spring Security Filter Chain
@EnableWebSecurity // @EnableWebSecurity will help me to create bean named ‘springSecurityFilterChain’
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    // Bean for UserDetailsService:
    // interface UserDetailsService cung cấp method loadUsersByUsername()
    // Nó cung cấp 2 implementation class là InMemoryUserDetailManager và JdbcUserDetailsManagers
    // Hoặc như bên dưới ta đang sử dụnh CUSTOM implementation class là ShoppingUserDetailsService
    @Bean
    public UserDetailsService userDetailsService()
    {
        return new ShoppingUserDetailsService();
    }

    // Bean for PasswordEncoder:
    @Bean
    public PasswordEncoder passwordEncoder()
    {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder;
    }


    // Bean for AuthenticationProvider
    // interface AuthenticationProvider cung cấp method authenticate() dùng để authenticate User (hay obj Authentication)
    // DaoAuthenticationProvider sẽ sử dụng method loadUsersByUsername() của UserDetailService và method matches() của PasswordEncoder để authenticate User
    @Bean
    public AuthenticationProvider authenticationProvider()
    {
        // DaoAuthenticationProvider : chính là implementation class của inteface AuthenticationProvider :
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // register custom AuthenticationProvider với AuthenticationManagerBuilder:
        auth
            .authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                    .loginPage("/login")
                    .usernameParameter("email") // Mặc định thì usernameParameter là username,
                                                // nhưng bên form ta để name của input là email nên phải sửa lại tên param ở đây
                    .defaultSuccessUrl("/")
                    //.failureHandler(new CustomAuthenticationUserHandler()) // handle custom error message when authentication fail
                    .permitAll()
                .and()
                .logout().permitAll(); // handle logout
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // ignore authentication với các folder dưới:
        web.ignoring().antMatchers("/images/**", "/js/**", "/css/**", "/webjars/**");
    }
}
