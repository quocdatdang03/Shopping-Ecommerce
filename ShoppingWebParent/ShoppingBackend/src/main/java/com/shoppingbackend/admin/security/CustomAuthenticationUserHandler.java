package com.shoppingbackend.admin.security;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


// Custom AuthenticationUserHandler : cho việc handle when authentication fail
public class CustomAuthenticationUserHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        HttpSession session = request.getSession();
        // Xử lý khi authentication failed :
            // Xử lý message error :
        String errorMessage = null;
        // Nếu error là User is disabled
        if(exception instanceof DisabledException)
        {
            errorMessage = "Your account is disabled!";
        }
        else if(exception instanceof BadCredentialsException)
        {
            // Nếu error là username or password is incorrect :
            errorMessage = "UserName or password is incorrect!";
        }

        session.setAttribute("authenticationErrorMessage", errorMessage);
        // Lấy ra root context path
        String contextPath = request.getContextPath();

        // Chuyển hướng tới trang login kèm errorMessage:
        response.sendRedirect(contextPath+"/login");
    }
}
