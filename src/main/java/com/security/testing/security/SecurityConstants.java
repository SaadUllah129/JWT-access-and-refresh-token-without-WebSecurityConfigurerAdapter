package com.security.testing.security;

public class SecurityConstants {
	
	public static final Long JWT_EXPIRATION = 7*24*60*60*1000L;
	public static final Long JWT_REFRESH_EXPIRATION = 30*24*60*60*1000L;
	public static final String JWT_SECRET = "securesecuresecuresecuresecuresecure";
	public static final String ROLES = "roles";
	public static final String TOKEN_PREFIX = "Bearer "; 
	public static final String APPLICATION_JSON_VALUE = "application/json";
	public static final String AUTHORIZATION = "authorization";

}
