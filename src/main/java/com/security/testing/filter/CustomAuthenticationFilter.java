package com.security.testing.filter;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.testing.security.SecurityConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter{
	
	private final AuthenticationManager authenticationManager;
	
	@Autowired
	public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
		super();
		this.authenticationManager = authenticationManager;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		String userName = request.getParameter(SPRING_SECURITY_FORM_USERNAME_KEY);
		String password = request.getParameter(SPRING_SECURITY_FORM_PASSWORD_KEY);
		UsernamePasswordAuthenticationToken authenticationToken = 
				new UsernamePasswordAuthenticationToken(userName, password);
		
		return authenticationManager.authenticate(authenticationToken);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authentication) throws IOException, ServletException {
		User principal = (User) authentication.getPrincipal();
		String access_token = Jwts.builder()
				.setSubject(principal.getUsername())
				.signWith(SignatureAlgorithm.HS512, SecurityConstants.JWT_SECRET)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis()+ SecurityConstants.JWT_EXPIRATION))
				.setIssuer(request.getRequestURI().toString())
				.claim(SecurityConstants.ROLES,
						principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.compact();
		
		String refresh_tocken = Jwts.builder()
				.setSubject(principal.getUsername())
				.signWith(SignatureAlgorithm.HS512, SecurityConstants.JWT_SECRET)
				.setExpiration(new Date(System.currentTimeMillis()+ SecurityConstants.JWT_EXPIRATION))
				.setIssuer(request.getRequestURI().toString())
				.compact();
//		response.setHeader("access_token", access_token);
//		response.setHeader("refresh_tocken", refresh_tocken);
		Map<String, String> tokens = new HashMap<>();
		tokens.put("access_token", access_token);
		tokens.put("refresh_tocken", refresh_tocken);
		response.setContentType(SecurityConstants.APPLICATION_JSON_VALUE);
		new ObjectMapper().writeValue(response.getOutputStream(), tokens);
	}
	

}
