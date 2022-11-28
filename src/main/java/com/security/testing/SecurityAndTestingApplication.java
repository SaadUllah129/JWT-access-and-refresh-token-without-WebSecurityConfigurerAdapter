package com.security.testing;


import java.util.ArrayList;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.security.testing.entity.AppUser;
import com.security.testing.entity.Role;
import com.security.testing.service.UserService;

@SpringBootApplication
public class SecurityAndTestingApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityAndTestingApplication.class, args);
	}
	
	@Bean
	CommandLineRunner run(UserService userService) {
		return args -> {
			userService.saveRole(new Role(null, "ROLE_USER"));
			userService.saveRole(new Role(null, "ROLE_ADMIN"));
			userService.saveRole(new Role(null, "ROLE_MANAGER"));
			userService.saveRole(new Role(null, "ROLE_SUPER_ADMIN"));
			
			userService.saveUser(new AppUser(null, "Saad Ullah", "saad", "1234", new ArrayList<>()));
			userService.saveUser(new AppUser(null, "Will Smith", "will", "1234", new ArrayList<>()));
			userService.saveUser(new AppUser(null, "Jim Carry", "jim", "1234", new ArrayList<>()));
			userService.saveUser(new AppUser(null, "John travolta", "john", "1234", new ArrayList<>()));
			userService.saveUser(new AppUser(null, "abid", "abid", "1234", new ArrayList<>()));
			
			userService.addRoleToUser("saad", "ROLE_ADMIN");
			userService.addRoleToUser("will", "ROLE_ADMIN");
			userService.addRoleToUser("saad", "ROLE_MANAGER");
			userService.addRoleToUser("john", "ROLE_USER");
			userService.addRoleToUser("abid", "ROLE_SUPER_ADMIN");
			userService.addRoleToUser("jim", "ROLE_USER");
			
		};
		
	}

}
