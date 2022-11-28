package com.security.testing.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.testing.entity.AppUser;
import com.security.testing.repository.UserRepository;
import com.security.testing.security.SecurityConstants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JwtAuthorizationFilter extends OncePerRequestFilter{
	
	private UserRepository userRepository;
	
	@Autowired	
	public JwtAuthorizationFilter(UserRepository userRepository) {
		super();
		this.userRepository = userRepository;
	}


	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if (request.getServletPath().equals("/api/login") || request.getServletPath().equals("api/v1/token/refresh/**")) {
			filterChain.doFilter(request, response);
			
		} else {
			String authorizationHeader = request.getHeader(SecurityConstants.AUTHORIZATION);
			if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith(SecurityConstants.TOKEN_PREFIX)) 
			{
				try {
					String token = authorizationHeader.substring(SecurityConstants.TOKEN_PREFIX.length());
					Claims claims = Jwts.parser()
							.setSigningKey(SecurityConstants.JWT_SECRET)
							.parseClaimsJws(token)
							.getBody();
					String username = claims.getSubject();
					AppUser user = userRepository.findByUserName(username);
					
					Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
					user.getRoles().forEach(role -> {
					authorities.add(new SimpleGrantedAuthority(role.getName()));
					}
					);
					
					UsernamePasswordAuthenticationToken authenticationToken = 
							new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword(),
									authorities);
					
					authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authenticationToken);
					filterChain.doFilter(request, response);
				} catch (Exception exception) {
					response.setHeader("error",exception.getMessage());
					Map<String, String> error = new HashMap<>();
					error.put("error_message",exception.getMessage());
					response.setContentType(SecurityConstants.APPLICATION_JSON_VALUE);
					new ObjectMapper().writeValue(response.getOutputStream(), error);
				}
			} else {
				filterChain.doFilter(request, response);
			}
			
		}
		
	}
	
	

}
