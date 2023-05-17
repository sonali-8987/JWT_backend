package com.thoughtworks.jwt.config;

import com.thoughtworks.jwt.helper.JwtUtil;
import com.thoughtworks.jwt.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtTokenFilter extends OncePerRequestFilter
{
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException 
    {
        //get jwt
        if (!hasAuthorizationBearer(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = getAccessToken(request);

        if (!jwtUtil.validateAccessToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        setAuthenticationContext(token, request);
        filterChain.doFilter(request, response);
    }

    private void setAuthenticationContext(String token, HttpServletRequest request)
    {
        UserDetails userDetails = getUserDetails(token);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

    }

    private UserDetails getUserDetails(String token) {
        String username = jwtUtil.extractUsername(token);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        return userDetails;
    }

    private String getAccessToken(HttpServletRequest request)
    {
        String requestTokenHeader = request.getHeader("Authorization");
            String token = requestTokenHeader.substring(7);
            return token;
    }

    private boolean hasAuthorizationBearer(HttpServletRequest request)
    {
        String requestTokenHeader = request.getHeader("Authorization");
        if (ObjectUtils.isEmpty(requestTokenHeader) || !requestTokenHeader.startsWith("Bearer")) {
            return false;
        }
        return true;
    }
}
