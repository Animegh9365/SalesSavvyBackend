package com.example.demo.service;

import java.math.BigDecimal;
import java.util.List;

import com.example.demo.entity.OrderItem;
import com.razorpay.RazorpayException;

public interface PaymentService {

	public String createOrder(int userId, BigDecimal totalAmount, List<OrderItem> cartItems) throws RazorpayException;
	
	public boolean verifyPayment(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature, int userId);
	
	public void saveOrderItems(String orderId, List<OrderItem> items);
}
