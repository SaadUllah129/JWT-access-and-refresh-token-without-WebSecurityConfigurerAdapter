package com.security.testing.controller;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.testing.entity.AppUser;
import com.security.testing.entity.Role;
import com.security.testing.security.SecurityConstants;
import com.security.testing.service.UserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@RestController
@RequestMapping("api/v1")
public class UserController {
	
	private UserService userService;
	@Autowired
	public UserController(UserService userService) {
		super();
		this.userService = userService;
	}



	@GetMapping("/users")
	public ResponseEntity<List<AppUser>>getUsers(){
		return new ResponseEntity<List<AppUser>>(userService.getAllUser(), HttpStatus.OK); 
	}
	
	@PostMapping("/users/new")
	public ResponseEntity<AppUser> saveUser(@RequestBody AppUser user){	
		return new ResponseEntity<AppUser>(userService.saveUser(user),HttpStatus.CREATED);
	}
	@PostMapping("/role/new")
	public ResponseEntity<String> saveRole(@RequestBody RoletoUser role){	
		userService.addRoleToUser(role.getUsername(), role.getRoleName());
		return new ResponseEntity<String>("Role added to user",HttpStatus.CREATED);
	}
	
	@PostMapping("/role/addtouser")
	public ResponseEntity<String> addRoleToUser(@RequestBody RoletoUser role){	
		userService.addRoleToUser(role.getUsername(), role.getRoleName());
		return new ResponseEntity<String>("Role added to user",HttpStatus.CREATED);
	}
	
	@PostMapping("/token/refresh")
	public void refreshToken(HttpServletResponse response, HttpServletRequest request) throws IOException {
		String authorizationHeader = request.getHeader(SecurityConstants.AUTHORIZATION);
		if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith(SecurityConstants.TOKEN_PREFIX)) 
		{
			try {
				String refresh_token = authorizationHeader.substring(SecurityConstants.TOKEN_PREFIX.length());
				Claims claims = Jwts.parser()
						.setSigningKey(SecurityConstants.JWT_SECRET)
						.parseClaimsJws(refresh_token)
						.getBody();
				String username = claims.getSubject();
				AppUser user = userService.getUser(username);
				String access_token = Jwts.builder()
						.setSubject(user.getUserName())
						.signWith(SignatureAlgorithm.HS512, SecurityConstants.JWT_SECRET)
						.setIssuedAt(new Date(System.currentTimeMillis()))
						.setExpiration(new Date(System.currentTimeMillis()+ SecurityConstants.JWT_EXPIRATION))
						.setIssuer(request.getRequestURI().toString())
						.claim(SecurityConstants.ROLES,
								user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
						.compact();
				Map<String, String> tokens = new HashMap<>();
				tokens.put("access_token", access_token);
				tokens.put("refresh_tocken", refresh_token);
				response.setContentType(SecurityConstants.APPLICATION_JSON_VALUE);
				new ObjectMapper().writeValue(response.getOutputStream(), tokens);
				
			} catch (Exception exception) {
				response.setHeader("error",exception.getMessage());
				Map<String, String> error = new HashMap<>();
				error.put("error_message",exception.getMessage());
				response.setContentType(SecurityConstants.APPLICATION_JSON_VALUE);
				new ObjectMapper().writeValue(response.getOutputStream(), error);
			}
		} else {
			throw new RuntimeException("refresh token is missing");
		}
	}
	
	
	class RoletoUser{
		private String username;
		private String roleName;
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public String getRoleName() {
			return roleName;
		}
		public void setRoleName(String roleName) {
			this.roleName = roleName;
		}
		
		
	}
}
