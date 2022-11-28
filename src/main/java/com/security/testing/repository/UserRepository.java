package com.security.testing.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.security.testing.entity.AppUser;

public interface UserRepository extends JpaRepository<AppUser, Long>{
	AppUser findByUserName(String username);
}
