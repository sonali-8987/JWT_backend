package com.thoughtworks.jwt.controller;


import com.thoughtworks.jwt.helper.JwtUtil;
import com.thoughtworks.jwt.model.JwtRequest;
import com.thoughtworks.jwt.model.JwtResponse;
import com.thoughtworks.jwt.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class JwtController
{
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity generateToken(@RequestBody JwtRequest jwtRequest) throws Exception {
        String token;
        try {
            this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(),jwtRequest.getPassword()));

            UserDetails userDetails =  this.customUserDetailsService.loadUserByUsername(jwtRequest.getUsername());
            token = this.jwtUtil.generateAccessToken(userDetails);
            System.out.println("JWT"+ token);

        }
        catch (BadCredentialsException e){
            e.printStackTrace();
            return new ResponseEntity<>("Bad Credentials",HttpStatus.UNAUTHORIZED);
        }
       return new ResponseEntity<>(new JwtResponse(token),HttpStatus.OK);

    }
}
