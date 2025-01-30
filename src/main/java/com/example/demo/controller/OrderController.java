package com.example.demo.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.User;
import com.example.demo.service.OrderService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins="http://localhost:5173", allowCredentials = "true")
@RequestMapping("/api/orders")
public class OrderController {

	@Autowired
	private OrderService orderService;
	
	@GetMapping
	public ResponseEntity<Map<String,Object>> getOrdersForUser(HttpServletRequest request) {
		
		try {
			// Retrieve the authenticated user from the request
			User authenticatedUser = (User) request.getAttribute("authenticatedUser");
			
			// Handle Unauthenticated request
			if (authenticatedUser == null) {
				return ResponseEntity.status(401).body(Map.of("error", "User not authenticateed"));
			}
			
			// Fetch orders for the user via the service layer
			Map<String, Object> response = orderService.getOrdersForUser(authenticatedUser);
			
			// Return the response with 200 ok
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
		} catch (Exception e) {
			// Handle unexpected exceptions
			e.printStackTrace();
			return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occured"));
		}
		
	}
}
