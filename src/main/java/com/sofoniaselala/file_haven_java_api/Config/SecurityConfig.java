package com.sofoniaselala.file_haven_java_api.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import com.sofoniaselala.file_haven_java_api.Services.UserDetailsServiceImpl;

import jakarta.servlet.http.HttpServletResponse;


/*
 * define permissions/accesses
 */


@Configuration
@EnableWebSecurity   // These annotations tells spring security to use our custom security configuration instead of default.
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService, JwtAuthFilter jwtAuthFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        return http
            .cors(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    //        Set permissions on endpoints
            .authorizeHttpRequests(auth -> auth
    //            our public endpoints
                .requestMatchers(HttpMethod.POST, "/signup").permitAll()
                .requestMatchers(HttpMethod.POST, "/login").permitAll()
    //            our private endpoints
                .anyRequest().authenticated())
                .logout(logout -> logout
                // Disable redirect after logout (at default route /logout)
                .logoutSuccessHandler((request, response, authentication) -> {
                     new SecurityContextLogoutHandler().logout(request, null, null); // response/second arg is null bc redirect is done on client side
                    // set accessToken to cookie header
                    ResponseCookie cookie = ResponseCookie.from("accessToken", null)
                            .httpOnly(true)
                            .secure(false)
                            .path("/")
                            .maxAge(0) // effectively expires the cookie immediately / clears the cookie from client
                            .build();
                    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
                    response.setStatus(HttpServletResponse.SC_OK); // Respond with 200 OK
                }))
            .authenticationManager(authenticationManager)
    //            Add JWT token filter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    // Receives requests from filters and delegates the validation of user details to the available authentication providers. 
    // It manages one or multiple authentication providers.
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }
}