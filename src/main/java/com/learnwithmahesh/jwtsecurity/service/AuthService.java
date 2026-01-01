package com.learnwithmahesh.jwtsecurity.service;

import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.learnwithmahesh.jwtsecurity.dto.LoginRequest;
import com.learnwithmahesh.jwtsecurity.dto.RefreshTokenRequest;
import com.learnwithmahesh.jwtsecurity.dto.RegisterRequest;
import com.learnwithmahesh.jwtsecurity.dto.TokenPair;
import com.learnwithmahesh.jwtsecurity.model.User;
import com.learnwithmahesh.jwtsecurity.repository.UserRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
public class AuthService {

	private final UserRepository userRepository;
	
	private final PasswordEncoder passwordEncoder;
	
	private final AuthenticationManager authenticationManager;
	
	private final JwtService jwtService;
	
	private final UserDetailsService userDetailsService ;

	

	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager, JwtService jwtService, UserDetailsService userDetailsService) {
		super();
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
	}

	@Transactional
	public User registerUser(RegisterRequest registerRequest)
	{
		//first we have to check where username is already exists
		Optional<User> userdata = userRepository.findByUsername(registerRequest.getUsername());
		if(userdata.isPresent())
		{
			throw new RuntimeException("User is already exists");
		}
		
		//here converting RegisterRequest to User
		
		User user=new User();
		user.setUsername(registerRequest.getUsername());
		user.setFullName(registerRequest.getFullName());
		user.setRole(registerRequest.getRole());
		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
		
		User registeredUser = userRepository.save(user);
		return registeredUser;
		
	}
	
	public TokenPair login(LoginRequest loginRequest)
	{
		//Authenticate the user
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), 
						loginRequest.getPassword()));
		
		//Set authentication in security context
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		//Generate the Token Pair
		TokenPair tokenPair = jwtService.generateTokenPair(authentication);
		return tokenPair;
	}

	public TokenPair refreshToken(@Valid RefreshTokenRequest refreshToken) {

		//check if it is a valid refresh token
		if(!jwtService.isRefreshToken(refreshToken.getRefreshToken()))
		{
			throw new IllegalArgumentException("Invalid Refresh Token...");
		}
		String user =jwtService.extractUsernameFromToken(refreshToken.getRefreshToken());
		
		UserDetails userDetails = userDetailsService.loadUserByUsername(user);
		if(userDetails==null)
		{
			throw new IllegalArgumentException("User not found");
		}
		UsernamePasswordAuthenticationToken authentication=new 
				UsernamePasswordAuthenticationToken(userDetails, null,userDetails.getAuthorities());
		
		String accesToken = jwtService.generateAccesToken(authentication);
		
		return new TokenPair(accesToken, refreshToken.getRefreshToken());
	}
	
	
}
