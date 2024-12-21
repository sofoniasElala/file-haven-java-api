package com.sofoniaselala.file_haven_java_api.Services;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sofoniaselala.file_haven_java_api.Helpers.AppUserDetails;
import com.sofoniaselala.file_haven_java_api.Model.User;
import com.sofoniaselala.file_haven_java_api.Repository.UserRepository;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(final UserRepository userRepository){
        this.userRepository = userRepository;
    }
    // Internally called by authenticationManager.authenticate() / Spring to get user record during log in request for authentication manager to verify password
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        Optional<User> userOptional = this.userRepository.findByUsernameIgnoreCase(username);
        if(!userOptional.isPresent()){
            throw new UsernameNotFoundException("User not found");
        }
        User user = userOptional.get();
        return new AppUserDetails(user);
    }

    public UserDetails loadUserById(Integer id){
        Optional<User> userOptional = this.userRepository.findById(id);
        if(!userOptional.isPresent()){
            throw new UsernameNotFoundException("User not found");
        }
        User user = userOptional.get();
        return new AppUserDetails(user);
    }

}