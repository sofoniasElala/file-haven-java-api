package com.sofoniaselala.file_haven_java_api.Helpers;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.sofoniaselala.file_haven_java_api.Model.User;

public class AppUserDetails implements UserDetails {
    private Integer id;
    private String username;
    private String password;

    public AppUserDetails(User user){
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
    }
    public Integer getId(){
        return this.id;
    }
    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Return an empty collection since there are no roles
        return Collections.emptyList();
    }
    
}