package com.example.demo.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.entity.CartItem;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.enums.OrderStatus;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import jakarta.transaction.Transactional;

@Service
public class PaymentServiceImplementation implements PaymentService {
	
	@Value("${razorpay.key_id}")
	private String razorpayKeyId;
	
	@Value("${razorpay.key_secret}")
	private String razorpayKeySecret;
	
	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;
	private final CartRepository cartRepository;
	
	public PaymentServiceImplementation(OrderRepository orderRepository, OrderItemRepository orderItemRepository, CartRepository cartRepository ) {
		this.orderRepository = orderRepository;
		this.orderItemRepository = orderItemRepository;
		this.cartRepository = cartRepository;
	}

	@Override
	@Transactional
	public String createOrder(int userId, BigDecimal totalAmount, List<OrderItem> cartItems) throws RazorpayException {
		// Create razorpay Client
		RazorpayClient  razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
		
		// Prepare razorpay order request
		var orderRequest = new JSONObject();
		orderRequest.put("amount", totalAmount.multiply(BigDecimal.valueOf(100)).intValue()); // Amount in paise
		orderRequest.put("currency", "INR");
		orderRequest.put("receipt", "txn_" + System.currentTimeMillis());
		
		// Create razorpay Order
		com.razorpay.Order razorpayOrder =  razorpayClient.orders.create(orderRequest);
		
		// Save order details in the database
		Order order = new Order();
		order.setOrderId(razorpayOrder.get("id"));
		order.setUserId(userId);
		order.setTotalAmount(totalAmount);
		order.setStatus(OrderStatus.PENDING);
		order.setCreatedAt(LocalDateTime.now());
		orderRepository.save(order);
		
		return razorpayOrder.get("id");
	}

	@Override
	@Transactional
	public boolean verifyPayment(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature,
			int userId) {

		try {
			// Prepare Signature validation attributes
			JSONObject attributes =  new JSONObject();
			attributes.put("razorpay_order_id", razorpayOrderId);
			attributes.put("razorpay_payment_id", razorpayPaymentId);
			attributes.put("razorpay_signature", razorpaySignature);
			System.out.println("orderid "+ razorpayOrderId);
			System.out.println("paymentid " + razorpayPaymentId);
			System.out.println("signature " + razorpaySignature);
			
			// Verify razorpay Signature
			boolean isSignatureValid = com.razorpay.Utils.verifyPaymentSignature(attributes, razorpayKeySecret);
			System.out.println("isSignatureValid: "+ isSignatureValid);
			
			if (isSignatureValid) {
				// Update order status to SUCCESS
				Order order = orderRepository.findById(razorpayOrderId).orElseThrow(()-> new RuntimeException("Order not found."));
				order.setStatus(OrderStatus.SUCCESS);
				order.setUpdatedAt(LocalDateTime.now());
				orderRepository.save(order);
				
				// Fetch cart items for the user
				List<CartItem> cartItems = cartRepository.findCartItemsWithProductDetails(userId);
				
				//Save Order items
				for (CartItem item: cartItems) {
					OrderItem orderItem = new OrderItem();
					orderItem.setOrder(order);
					orderItem.setProductId(item.getProduct().getProductId());
					orderItem.setQuantity(item.getQuantity());
					orderItem.setPricePerUnit(item.getProduct().getPrice());
					
					orderItem.setTotalPrice(item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
					orderItemRepository.save(orderItem);
				}
				
				// Clear users Cart
				cartRepository.deleteAllCartItemsByUserId(userId);
				
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	@Transactional
	public void saveOrderItems(String orderId, List<OrderItem> items) {
		Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
		
		for (OrderItem item: items) {
			item.setOrder(order);
			orderItemRepository.save(item);
		}
		
	}

}
