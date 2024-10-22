package com.shopping.web.security.oauth;

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
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    @Lazy
    private CustomerServiceImpl customerService;

   // This method will be invoked by Spring Security upon successful authentication by oauth 2
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {

        // get Customer info when successful authentication :
        CustomerOAuth2User oauth2User = (CustomerOAuth2User) authentication.getPrincipal();

        String name = oauth2User.getName();
        String email = oauth2User.getEmail();
        String countryCode = request.getLocale().getCountry();

        Customer customer = customerService.getCustomerByEmail(email);
        if(customer==null) {
            // if this email didn't register account in DB
            // add new account with this email
            customerService.addNewCustomerUponOAuthLogin(name, email, countryCode);
        }
        else
        {
            // if this email was registered account in DB
            // update authentication type for this account:
            customerService.updateAuthenticationType(customer, AuthenticationType.GOOGLE);
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
