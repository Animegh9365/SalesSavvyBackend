package com.example.demo.dto;

import org.springframework.stereotype.Component;

@Component
public class PasswordResetDTO {

	private String password;
	private String userId;
	
	public PasswordResetDTO() {
		
	}

	public PasswordResetDTO(String password, String userId) {
		super();
		this.password= password;
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
}
