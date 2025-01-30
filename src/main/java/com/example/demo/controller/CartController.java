package com.example.demo.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CartService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/api/cart/")
public class CartController {

	UserRepository userRepository;
	CartService cartService;
	public CartController(UserRepository userRepository, CartService cartService) {
		this.userRepository = userRepository;
		this.cartService = cartService;
	}
	
	@GetMapping("/items/count")
	public ResponseEntity<Integer> gettCartItemCount(@RequestParam String username) {
		User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));
		int count = cartService.getCartItemCount(user.getId());
		return ResponseEntity.ok(count);
	}
	
	
	@PostMapping("/add")
	public ResponseEntity<Void> addToCart(@RequestBody Map<String, Object> request) {
		
		String username = (String) request.get("username");
		int productId = (int) request.get("productId");
		
		// Handle quantity: Default to 1 if  not provided
		int quantity = request.containsKey("quantity")? (int) request.get("quantity"): 1;
		
		// Fetch the user using username
		User user = userRepository.findByUsername(username).orElseThrow(()-> new IllegalArgumentException("User not found with username: " + username));
		
		cartService.addToCart(user.getId(), productId, quantity);
		return ResponseEntity.status(HttpStatus.CREATED).build();
		
	}
	
	@GetMapping("/items")
	public ResponseEntity<Map<String, Object>> getCartItems(HttpServletRequest request) {
		// Fetch user by username to get the userId
		User user = (User) request.getAttribute("authenticatedUser");
		
		// Call the service to get Cart items for the user
		Map<String, Object> cartItems = cartService.getCartItems(user.getId());
		return ResponseEntity.ok(cartItems);
		
	}
	
	@PutMapping("/update")
	public ResponseEntity<Void> updateCartItemQuantity(@RequestBody Map<String, Object> request) {
		String username = (String)request.get("username");
		int productId = (int)request.get("productId");
		int quantity = (int)request.get("quantity");
		System.out.println("Quantity: " + quantity);
		
		// Fetch the user using usename
		User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));
		
		// Update the cart item quantity
		cartService.updateCartItemQuantity(user.getId(), productId, quantity);
		return ResponseEntity.status(HttpStatus.OK).build();
		
	}
	
	@DeleteMapping("/delete")
	public ResponseEntity<Void> deleteCartItem(@RequestBody Map<String, Object> request) {
		String username = (String)request.get("username");
		int productId = (int)request.get("productId");
		
		User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("User not found with username : "+ username));
		
		//Delete the cart Item
		cartService.deleteCartItem(user.getId(), productId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
	
}
