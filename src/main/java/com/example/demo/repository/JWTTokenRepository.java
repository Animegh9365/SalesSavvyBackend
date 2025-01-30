package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.JWTToken;

import jakarta.transaction.Transactional;


@Repository
public interface JWTTokenRepository extends JpaRepository<JWTToken, Integer>{

	Optional<JWTToken> findByToken(String token);
	
	@Query("SELECT t FROM JWTToken t WHERE t.user.user_id = :userId")
	JWTToken findByUserId(@Param("userId") int userId);
	
	@Modifying
	@Transactional
	@Query("DELETE FROM JWTToken t WHERE t.user.user_id = :userId")
	void deleteByUserId(@Param("userId") int userId);
	
}
