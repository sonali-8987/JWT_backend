package com.thoughtworks.jwt.helper;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

//methods for generating token
//validate method
//isExp
//util class for jwt
@Component
public class JwtUtil
{

    private String SECRET_KEY;
    private int EXPIRE_DURATION_IN_MS;

    @Value("${jwt.secret}")
    public void setSecret(String secret) {
        SECRET_KEY = secret;
    }

    @Value("${jwt.expirationDateInMs}")
    public void setJwtExpirationInMs(int jwtExpirationInMs) {
        EXPIRE_DURATION_IN_MS = jwtExpirationInMs;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtil.class);

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateAccessToken(UserDetails userDetails) {
        String subject = userDetails.getUsername();
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_DURATION_IN_MS))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }


    public Boolean validateAccessToken(String token) {
       try{
           Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
           return  true;
       }
        catch (IllegalArgumentException ex) {
           LOGGER.error("Token is null, empty or only whitespace", ex.getMessage());
       } catch (MalformedJwtException ex) {
           LOGGER.error("JWT is invalid", ex);
       } catch (UnsupportedJwtException ex) {
           LOGGER.error("JWT is not supported", ex);
       } catch (SignatureException ex) {
           LOGGER.error("Signature validation failed");
       }
        return false;


    }

}
