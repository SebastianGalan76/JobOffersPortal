package com.coresaken.jobportal.config;

import java.io.IOException;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class UrlRedirectFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String serverName = httpRequest.getServerName();

        if (serverName.equalsIgnoreCase("www.joingamedev.pl")) {
            String redirectUrl = "https://joingamedev.pl" + httpRequest.getRequestURI();
            httpResponse.sendRedirect(redirectUrl);
            return;
        }

        chain.doFilter(request, response);
    }
}
