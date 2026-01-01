package com.learnwithmahesh.jwtsecurity.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learnwithmahesh.jwtsecurity.dto.LoginRequest;
import com.learnwithmahesh.jwtsecurity.dto.RefreshTokenRequest;
import com.learnwithmahesh.jwtsecurity.dto.RegisterRequest;
import com.learnwithmahesh.jwtsecurity.dto.TokenPair;
import com.learnwithmahesh.jwtsecurity.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	private final AuthService authService;
	
	public AuthController(AuthService authService) {
		super();
		this.authService = authService;
	}


    @PostMapping("/register")
	public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest)
	{
		//Save a new user to database 
    	authService.registerUser(registerRequest);
	   return ResponseEntity.ok("User registered successfully");	
	}
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest)
    {
    	//authenticate the user
    	//return access token and refresh token
    	
    	TokenPair tokenPair = authService.login(loginRequest);
    	
    	return ResponseEntity.ok(tokenPair);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshToken)
    {
    	TokenPair tokenPair=authService.refreshToken(refreshToken);
    	return ResponseEntity.ok(tokenPair);
    }
}
