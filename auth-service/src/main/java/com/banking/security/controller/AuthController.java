package com.banking.security.controller;

import org.apache.catalina.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banking.security.dto.LoginRequest;
import com.banking.security.dto.RegisterRequest;
import com.banking.security.entity.UserMaster;
import com.banking.security.exceptionHandler.InvalidCredentialsException;
import com.banking.security.exceptionHandler.UserNotFoundException;
import com.banking.security.jwtUtils.JwtUtility;
import com.banking.security.repository.UserRepository;
import com.banking.security.responseHandlers.AuthResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {
	
	private final PasswordEncoder encoder;
	private final UserRepository repository;
	
	private final JwtUtility jwtUtility;
	
 
	private static final Logger LOGGER= LoggerFactory.getLogger(AuthController.class);


	

  public AuthController(PasswordEncoder encoder, UserRepository repository, JwtUtility jwtUtility) {
		super();
		this.encoder = encoder;
		this.repository = repository;
		this.jwtUtility = jwtUtility;
	}

@PostMapping("/register")
	public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest){
	 LOGGER.info("Reached Auth Controller : ");
	    if (registerRequest.getPassword()==null || registerRequest.getPassword().equals("")) {
			throw new RuntimeException("Password cannot be null or empty");
		}
		UserMaster master= new UserMaster();
		master.setUserName(registerRequest.getUserName());
		master.setEmail(registerRequest.getEmail());
		master.setRole(registerRequest.getRole());
		master.setPassword(encoder.encode(registerRequest.getPassword()));
		 LOGGER.info("Saving User Details : "+master);
		 if (repository.findByEmail(registerRequest.getEmail()).isPresent()) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body("User Already Exists"); 
			
		}
		repository.save(master);
		 LOGGER.info("saved User Details : "+master);
		return ResponseEntity.status(HttpStatus.CREATED).body(master);
		
	}
  
  @PostMapping("/login")
  public ResponseEntity<?>userLogin(@RequestBody LoginRequest loginRequest){
	  
	  UserMaster user= repository.findByEmail(loginRequest.getEmail())
			  .orElseThrow(()->new UserNotFoundException("User not found with Email : "+loginRequest.getEmail()));
	 
	if (!encoder.matches(loginRequest.getPassword(), user.getPassword())) {
		throw new InvalidCredentialsException("Invalid email or password");
		
	}
	String token= jwtUtility.generateToken(user.getUserName(), user.getRole());
	return ResponseEntity.ok(new AuthResponse(token));
	 
	  
  }
  
  @PostMapping("/internal/createUser")
  public ResponseEntity<String> createInternalUser(@RequestBody RegisterRequest req) {
	    UserMaster master= new UserMaster();
		master.setUserName(req.getUserName());
		master.setEmail(req.getEmail());
		master.setRole(req.getRole());
		master.setPassword(encoder.encode(req.getPassword()));
	  
	  repository.save(master);
      return ResponseEntity.ok("User created");
  }
  
  
	

}
