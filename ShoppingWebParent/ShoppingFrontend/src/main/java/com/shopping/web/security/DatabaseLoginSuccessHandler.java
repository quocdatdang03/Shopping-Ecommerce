package com.shopping.web.security;

import com.shopping.common.entity.AuthenticationType;
import com.shopping.common.entity.Customer;
import com.shopping.web.customer.service.CustomerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class DatabaseLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    @Lazy
    private CustomerServiceImpl customerService;

    // This method will be invoked by Spring Security upon successful authentication by login in database
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        // get Customer
        CustomerUserDetails customerUserDetails = (CustomerUserDetails) authentication.getPrincipal();
        Customer customer = customerUserDetails.getCustomer();

        // update authentication type equal to DATABASE :
        customerService.updateAuthenticationType(customer, AuthenticationType.DATABASE);

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
