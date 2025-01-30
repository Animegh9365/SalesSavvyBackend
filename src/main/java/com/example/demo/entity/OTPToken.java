package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="otp_token")
public class OTPToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer otpId;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	User user;
	
	@Column
	String otp;
	
	@Column
	LocalDateTime created_at;
	
	
	public OTPToken() {
	
	}


	public OTPToken(Integer otpId, User user, String otp, LocalDateTime created_at) {
		super();
		this.otpId = otpId;
		this.user = user;
		this.otp = otp;
		this.created_at = created_at;
	}


	public OTPToken(User user, String otp, LocalDateTime created_at) {
		super();
		this.user = user;
		this.otp = otp;
		this.created_at = created_at;
	}


	public Integer getOtpId() {
		return otpId;
	}


	public void setOtpId(Integer otpId) {
		this.otpId = otpId;
	}


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}


	public String getOtp() {
		return otp;
	}


	public void setOtp(String otp) {
		this.otp = otp;
	}


	public LocalDateTime getCreated_at() {
		return created_at;
	}


	public void setCreated_at(LocalDateTime created_at) {
		this.created_at = created_at;
	}
	
}
