package com.example.demo.dto;

import org.springframework.stereotype.Component;

@Component
public class OTPRequest {

	private String otp;
	
	public OTPRequest() {
		
	}
	
	public OTPRequest(String otp) {
		super();
		this.otp = otp;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}
}
