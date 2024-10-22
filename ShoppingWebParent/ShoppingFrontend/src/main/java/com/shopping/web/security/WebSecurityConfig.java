package com.shopping.web.security;

import com.shopping.web.security.oauth.CustomerOAuth2UserService;
import com.shopping.web.security.oauth.OAuth2LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
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


    @Autowired
    private CustomerOAuth2UserService customerOAuth2UserService;

    @Autowired
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Autowired
    private DatabaseLoginSuccessHandler databaseLoginSuccessHandler;



    // Bean for UserDetailsService:
    // interface UserDetailsService cung cấp method loadUsersByUsername()
    // Nó cung cấp 2 implementation class là InMemoryUserDetailManager và JdbcUserDetailsManagers
    // Hoặc như bên dưới ta đang sử dụnh CUSTOM implementation class là CustomerUserDetailsService
    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomerUserDetailsService();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean for AuthenticationProvider
    // interface AuthenticationProvider cung cấp method authenticate() dùng để authenticate User (hay obj Authentication)
    // DaoAuthenticationProvider sẽ sử dụng method loadUsersByUsername() của UserDetailService và method matches() của PasswordEncoder để authenticate User
    @Bean
    public AuthenticationProvider authenticationProvider()
    {
        // DaoAuthenticationProvider : chính là implementation class của inteface AuthenticationProvider :
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest()
                    .permitAll()
                    .and()
                        .formLogin()
                            .loginPage("/login")
                            .usernameParameter("email")
                            .defaultSuccessUrl("/")
                            .successHandler(databaseLoginSuccessHandler)
                            .permitAll()
                    .and()
                        .oauth2Login()
                            .loginPage("/login")
                            .userInfoEndpoint()
                            .userService(customerOAuth2UserService)
                        .and()
                            .successHandler(oAuth2LoginSuccessHandler)
                    .and()
                        .logout()
                            .permitAll()
                    .and()
                        .rememberMe() // enable remember me
                            .key("1212abcdefsad123456789") // set fixed private key để cookie remember-me vẫn được lưu khi restart project
                            .rememberMeParameter("remember-me") // chỉ định param nhận được từ bên form login là remember-me
                            .tokenValiditySeconds(7*24*60*60);// set expiration time of Cookie : 1 weeks (by default 2 weeks);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // ignore authentication với các folder dưới:
        web.ignoring().antMatchers("/images/**", "/js/**", "/css/**", "/webjars/**");
    }
}
