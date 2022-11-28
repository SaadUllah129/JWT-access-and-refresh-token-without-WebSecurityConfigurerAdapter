package com.security.testing.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.security.testing.entity.AppUser;
import com.security.testing.entity.Role;
import com.security.testing.repository.RoleRepository;
import com.security.testing.repository.UserRepository;
import com.security.testing.service.UserService;
import lombok.extern.slf4j.Slf4j;

@Service("UserServiceImpl") @Slf4j
public class UserServiceImpl implements UserService, UserDetailsService{
	private  final UserRepository userRepository;
	private  final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder; 
	
	@Autowired
	public UserServiceImpl(
			UserRepository userRepository,
			RoleRepository roleRepository,
			PasswordEncoder passwordEncoder) {
		super();
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public AppUser saveUser(AppUser user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

	@Override
	public Role saveRole(Role role) {
		return roleRepository.save(role);
	}

	@Override
	public void addRoleToUser(String userName, String roleName) {
		AppUser user = userRepository.findByUserName(userName);
		Role role = roleRepository.findByName(roleName);
		user.getRoles().add(role);
		userRepository.save(user);
		
	}

	@Override
	public AppUser getUser(String username) {
		return userRepository.findByUserName(username);
	}

	@Override
	public List<AppUser> getAllUser() {
		return userRepository.findAll();
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		AppUser user = userRepository.findByUserName(username);
		if (user == null) {
			System.out.println("user not found "+ username);
			throw new UsernameNotFoundException(username);

		} else {
			System.out.println("user found");
		}
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
		user.getRoles().forEach(role -> {
		authorities.add(new SimpleGrantedAuthority(role.getName()));
		}
		);
		return new User(user.getUserName(), user.getPassword(), authorities);
	}

}
