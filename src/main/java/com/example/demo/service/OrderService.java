package com.example.demo.service;

import java.util.Map;

import com.example.demo.entity.User;

public interface OrderService {

	public Map<String,Object> getOrdersForUser(User user);
}
