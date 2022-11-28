package com.security.testing.security;

public enum ApplicationUserPermission {
	STUDENT_READ("student:read"),
	STUDENT_WRITE("student:write"),
	COURSE_READ("course:read"),
	COURSE_WRITE("course:write"),
	USER_READ("user:read");

	private final String permission;
	
	ApplicationUserPermission(String string) {
		this.permission = string;
	}

	public String getPermission() {
		return permission;
	}
	

}
