package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.Product;

public interface ProductService {

	List<Product> getProductByCategory(String categoryName);
	
	List<String> getProductImages(Integer productId);
}
