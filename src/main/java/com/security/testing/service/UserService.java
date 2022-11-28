package com.security.testing.service;

import java.util.List;

import com.security.testing.entity.AppUser;
import com.security.testing.entity.Role;


public interface UserService {
	AppUser saveUser(AppUser user);
	Role saveRole(Role role);
	void addRoleToUser(String userName, String roleName);
	AppUser getUser(String username);
	
	List<AppUser> getAllUser();
}
