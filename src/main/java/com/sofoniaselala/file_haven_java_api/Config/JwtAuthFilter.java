package com.sofoniaselala.file_haven_java_api.Config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sofoniaselala.file_haven_java_api.Services.JwtService;
import com.sofoniaselala.file_haven_java_api.Services.UserDetailsServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * This filter is intended to intercept incoming HTTP requests by retrieving JWT tokens from cookies and authenticating users based on the extracted tokens if tokens are valid
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter { // OncePerRequestFilter is a base class provided by Spring Security that ensures a filter is executed once per HTTP request within a single request thread

    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final JwtService jwtService;

    public JwtAuthFilter(final UserDetailsServiceImpl userDetailsServiceImpl, JwtService jwtService){
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{
        String token = null;
        String userID = null;

        if(request.getCookies() != null){
            for(Cookie cookie: request.getCookies()){
                if(cookie.getName().equals("accessToken")){
                    token = cookie.getValue();
                }
            }
        }

        //If the accessToken is null. It will pass the request to next filter in the chain.
       //Any login and signup requests will not have jwt token in their header, therefore they will be passed to next filter chain. most likely to UsernamePasswordAuthenticationFilter.
      if (token == null) {
        filterChain.doFilter(request, response);
        return;
      }

      userID = jwtService.extractUseId(token);

      if(userID != null){
            UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(userID);
            if(jwtService.validateToken(token, userDetails)){
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, null); // acts as a container for authenticated user details, which can be passed around securely within the application
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken); // enables subsequent security checks (e.g., role-based access control) and allows retrieval of the user’s details from anywhere in the application.
            }

        }
        filterChain.doFilter(request, response); // allowing the request to proceed with the updated security context. most likely to UsernamePasswordAuthenticationFilter

    }
}