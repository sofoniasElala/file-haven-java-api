package com.sofoniaselala.file_haven_java_api.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.sofoniaselala.file_haven_java_api.Controller.DTOs.LoginRequest;
import com.sofoniaselala.file_haven_java_api.Controller.DTOs.SignupRequest;
import com.sofoniaselala.file_haven_java_api.Helpers.AppUserDetails;
import com.sofoniaselala.file_haven_java_api.Model.User;
import com.sofoniaselala.file_haven_java_api.Repository.UserRepository;
import com.sofoniaselala.file_haven_java_api.Services.JwtService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;


@RestController
public class UserController {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final  PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, AuthenticationManager authenticationManager, JwtService jwtService, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;

    }

    @GetMapping("/user/all")
    public Iterable<User> getUsers() {
        return this.userRepository.findAll();
    }
    

    @GetMapping("/user/{id}")
    @ResponseStatus(HttpStatus.FOUND)
    public User getUserById(@PathVariable("id") Integer id){
        Optional <User> userOptional =  this.userRepository.findById(id);

        if(!userOptional.isPresent()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User id "+ id + " does not exist.");

        User user = userOptional.get();
        return user;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response){
        Map<String, Object> responseBody = new HashMap<>();
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
    if(authentication.isAuthenticated()){
        String accessToken = jwtService.GenerateToken(((AppUserDetails)authentication.getPrincipal()).getId());
        // set accessToken to cookie header
        ResponseCookie cookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(60 * 60 * 24 * 14) // 2 weeks in seconds
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        responseBody.put("success", true);

    } else {
        responseBody.put("success", false);
    }
    return responseBody;

    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> signup(@Valid @RequestBody SignupRequest request, HttpServletResponse response){
        Map<String, Object> responseBody = new HashMap<>();
        if(!request.password().equals(request.confirmPassword())) {
             responseBody.put("success", false);
             responseBody.put("message", "Passwords don't match");
        } else {
             String username = request.username();
             Optional<User> existingUser = this.userRepository.findByUsernameIgnoreCase(username);
            if (existingUser.isPresent()) {
                responseBody.put("success", false);
                responseBody.put("message", String.format("User with the email address '%s' already exists.", username));
            } else {
                String hashedPassword = passwordEncoder.encode(request.password());
                User user = new User(username, hashedPassword);
                this.userRepository.save(user);
                responseBody.put("success", true);
            }
        }


    return responseBody;

    }
}