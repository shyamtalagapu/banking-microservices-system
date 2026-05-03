package com.banking.security.jwtUtils;


import java.security.Key;

import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtility {
	  private final String SECRET = "mysecretkeymysecretkeymysecretkey";
	  private Key getSigningKey() {
	        return Keys.hmacShaKeyFor(SECRET.getBytes());
	    }

	  //Generate Token
	  public String generateToken(String userName, String role) {
		  return Jwts.builder()
				  .setSubject(userName)
				  .claim("role", role)
				  .setIssuedAt(new Date())
				  .setExpiration(new Date(System.currentTimeMillis()+1000*60*60))
				  .signWith(getSigningKey(), SignatureAlgorithm.HS256)
				  .compact();
	  }
	 
	  
	  //Parsing the token
	  private Claims getClaims(String token) {
	        return Jwts.parserBuilder()
	                .setSigningKey(getSigningKey())
	                .build()
	                .parseClaimsJws(token)
	                .getBody();
	    }
	
	  //Extracting the User Name
	 public String extractUserName(String token) {
		 return getClaims(token).getSubject();
	 }
	 
	 //check token expiry
	 private boolean isTokenExpired(String token) {
	        return getClaims(token).getExpiration().before(new Date());
	    }
	 
	 //Validate the token
	 public boolean validateToken(String userName, String token) {
		final String finalExtractedName=extractUserName(userName);
		return finalExtractedName.equals(userName)&& !isTokenExpired(token);
		 
	 }
}
