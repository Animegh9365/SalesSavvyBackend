package com.example.demo.service;

import java.util.Map;

public interface CartService {

	public void addToCart(int userId, int productId, int quantity);
	
	public int getCartItemCount(int userId);
	
	public Map<String, Object> getCartItems(int userId);
	
	public void updateCartItemQuantity(int userId, int productId, int quantity);
	
	public void deleteCartItem(int userId, int productId);
}
