package com.example.demo.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.OrderItem;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.PaymentService;
import com.razorpay.RazorpayException;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins="http://localhost:5173", allowCredentials = "true")
@RequestMapping("/api/payment")
public class PaymentController {

	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private UserRepository userRepository;
	
	/*
	 * Create Razorpay Order
	 * @param requestBody Map containing totalAmount and cartItems
	 * @param request HttpServletRequest for authenticated user
	 * @return ResponseEntity with Razorpay Order Id
	 * */
	
	
	@PostMapping("/create")
	public ResponseEntity<String> createPaymentOrder(@RequestBody Map<String, Object> requestBody, HttpServletRequest request) {
		try {
			// Fetch authenticated user
			User user = (User) request.getAttribute("authenticatedUser");
			if (user == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
			}
			
			// Extract totalAmount and Cart Items from the request body
			BigDecimal totalAmount = new BigDecimal(requestBody.get("totalAmount").toString());
			List<Map<String,Object>> cartItemsRaw = (List<Map<String, Object>>) requestBody.get("cartItems");
			
			// Convert cartItemsRaw to List<OrderItem>
			List<OrderItem> cartItems = cartItemsRaw.stream().map(item -> {
				OrderItem orderItem = new OrderItem();
				orderItem.setProductId((Integer) item.get("productId"));
				orderItem.setQuantity((Integer) item.get("quantity"));
				BigDecimal pricePerUnit = new BigDecimal(item.get("price").toString());
				orderItem.setPricePerUnit(pricePerUnit);
				orderItem.setTotalPrice(pricePerUnit.multiply(BigDecimal.valueOf((Integer) item.get("quantity"))));
				return orderItem;
			}).collect(Collectors.toList());
			
			
			// Call the paymentService to create a RazorpayOrder
			String razorpayOrderId = paymentService.createOrder(user.getId(), totalAmount, cartItems);
			return ResponseEntity.ok(razorpayOrderId);
		} catch (RazorpayException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating Razorpay Order: " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request data: " + e.getMessage());
		}
	}
	
	@PostMapping("/verify")
	public ResponseEntity<String> verifyPayment(@RequestBody Map<String, Object> requestBody, HttpServletRequest request) {
		try {
			// Fetch Authenticated User
			User user = (User) request.getAttribute("authenticatedUser");
			if (user == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
			}
			
			int userId = user.getId();
			
			// Extract reazorpay payment details from the request body
			String razorpayOrderId = (String) requestBody.get("razorpayOrderId");
			String razorpayPaymentId = (String) requestBody.get("razorpayPaymentId");
			String razorpaySignature = (String) requestBody.get("razorpaySignature");
			
			// Call the payment service to verify the payment
			boolean isVerified = paymentService.verifyPayment(razorpayOrderId, razorpayPaymentId, razorpaySignature, userId);
			
			if (isVerified) {
				return ResponseEntity.ok("Payment verified successfully");
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment verification failed");	
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error verifying payment: " + e.getMessage());
		}
	}
}
