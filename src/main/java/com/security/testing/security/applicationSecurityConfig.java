package com.security.testing.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.security.testing.filter.CustomAuthenticationFilter;
import com.security.testing.filter.JwtAuthorizationFilter;
import com.security.testing.repository.UserRepository;

import static com.security.testing.security.ApplicationUserRole.*;
import static com.security.testing.security.ApplicationUserPermission.*;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class applicationSecurityConfig{
	
	// it helps to encode the password with BCRYPT,  without encryption we can't login to basic AUTH
	private final PasswordEncoder passwordEncoder;
	private final UserDetailsService userDetailsService;
	private UserRepository userRepository;
	//private JwtAuthEntryPoint enteryPoint;
	
	@Autowired
	public applicationSecurityConfig(
	PasswordEncoder passwordEncoder,
			 UserDetailsService userDetailsService,
			 UserRepository userRepository
			) {
		super();
		this.passwordEncoder = passwordEncoder;
		this.userDetailsService = userDetailsService;
		this.userRepository = userRepository;
	}

	// this is used to implement the basic AUTH security
		@Bean
	    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
			  // Configure AuthenticationManagerBuilder
	        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
	        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
			
	        // Get AuthenticationManager
	        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
	        CustomAuthenticationFilter authenticationFilter = new CustomAuthenticationFilter(authenticationManager);
	        authenticationFilter.setFilterProcessesUrl("/api/login");
			 http
			.csrf().disable()
			.exceptionHandling()
			//.authenticationEntryPoint(enteryPoint)
			.and()
	        .authorizeRequests()
	        .antMatchers("index","/css/**","/js/**").permitAll()
	        .antMatchers("/api/login/**","api/v1/token/refresh/**").permitAll()
	        .antMatchers("/api/v1/auth/**").permitAll()
	        .antMatchers("/api/v1/students**").hasRole(USER.name())
	        .antMatchers("/api/v1/users/**").hasAuthority(SUPER_ADMIN.name())
	        .antMatchers(HttpMethod.DELETE,"/management/api/**").hasAnyRole(SUPER_ADMIN.name())
	        .antMatchers(HttpMethod.PUT,"/management/api/**").hasAnyRole(ADMIN.name(),SUPER_ADMIN.name())
	        .antMatchers(HttpMethod.POST,"/management/api/**").hasAnyRole(ADMIN.name(),SUPER_ADMIN.name())
	        .antMatchers(HttpMethod.GET,"/management/api/**").hasAnyRole(ADMIN.name(),MANAGER.name(),SUPER_ADMIN.name())
	        .anyRequest()
	        .authenticated()
	        .and()
	        .addFilter(authenticationFilter)
	        .addFilterBefore(new JwtAuthorizationFilter(userRepository),UsernamePasswordAuthenticationFilter.class)
	        .authenticationManager(authenticationManager)
	        .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
			 return http.build();
	    }
		
//		@Bean
//		public UserDetailsService userDetailsService() {
//			UserDetails saadUllah = User.builder()
//					 .username("Saad")
//					 .password(passwordEncoder.encode("loser"))
//					 .authorities(ADMIN.getGrantedAuthorities())
//					 .roles(STUDENT.name())
//					 .build();
//			return new InMemoryUserDetailsManager(
//					 saadUllah
//					 );
//		}
	
}
