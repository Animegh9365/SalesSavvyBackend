package com.example.demo.service;

import java.util.Map;
import java.util.Optional;

import com.example.demo.entity.User;

public interface ForgotPasswordService {

	Optional<?> findUserByEmail(String email);
	
	public void sendOtp(User user);
	
	public Map<String,Object> verifyOtp(String otp);
	
	public User setNewPassword(Integer userId,String password);
}
