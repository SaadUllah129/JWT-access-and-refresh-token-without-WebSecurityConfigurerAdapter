package com.security.testing.security;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.google.common.collect.Sets;
import static com.security.testing.security.ApplicationUserPermission.*;

public enum ApplicationUserRole {

	
	USER(Sets.newHashSet(COURSE_READ)),
	
	ADMIN(Sets.newHashSet(
			COURSE_READ,
			COURSE_WRITE,
			STUDENT_READ,
			STUDENT_WRITE)),
	
	MANAGER(Sets.newHashSet(COURSE_READ,
			STUDENT_READ)),
	
	SUPER_ADMIN(Sets.newHashSet(COURSE_READ,
			COURSE_WRITE,
			STUDENT_READ,
			STUDENT_WRITE,
			USER_READ));
	
	
	private final Set<ApplicationUserPermission> permissions;

	private ApplicationUserRole(Set<ApplicationUserPermission> permissions) {
		this.permissions = permissions;
	}

	public Set<ApplicationUserPermission> getPermissions() {
		return permissions;
	}
	
	public Set<SimpleGrantedAuthority> getGrantedAuthorities(){
		Set<SimpleGrantedAuthority> permissions = getPermissions().stream()
		.map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
		.collect(Collectors.toSet());
		permissions.add(new SimpleGrantedAuthority("ROLE_"+this.name()));
		return permissions;
	}
	
	
	
	

}
