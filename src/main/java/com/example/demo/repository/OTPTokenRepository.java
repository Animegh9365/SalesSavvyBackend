package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.OTPToken;

@Repository
public interface OTPTokenRepository extends JpaRepository<OTPToken, Integer>{

	OTPToken findByOtp(String otp);
}
