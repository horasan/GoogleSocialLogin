package com.horasan.oauth2.intercepters;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

public class RestAuthEntryPointForUnauthenticatedCalls implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse,
                         AuthenticationException e) throws IOException, ServletException {
    	
        httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                "[Caller is 'unauthorized message'] Authentication required: " + e.getLocalizedMessage());
        
    }

}