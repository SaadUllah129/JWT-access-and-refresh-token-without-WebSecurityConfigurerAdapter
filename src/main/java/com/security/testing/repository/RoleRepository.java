package com.security.testing.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.security.testing.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
	
	Role findByName(String name);

}
