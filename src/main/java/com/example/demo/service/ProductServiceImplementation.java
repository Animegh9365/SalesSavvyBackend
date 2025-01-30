package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductImage;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductImageRepository;
import com.example.demo.repository.ProductRepository;

@Service
public class ProductServiceImplementation implements ProductService{
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private ProductImageRepository productImageRepository;

	@Override
	public List<Product> getProductByCategory(String categoryName) {
		if (categoryName != null && !categoryName.isEmpty()) {
			Optional<Category> categoryOpt =  categoryRepository.findByCategoryName(categoryName);
			if (categoryOpt.isPresent()) {
				Category category = categoryOpt.get();
				return productRepository.findByCategory_CategoryId(category.getCategoryId());
			} else {
				throw new RuntimeException("Category not found");
			}
		} else {
			return productRepository.findAll(); 
		}
		
	}

	@Override
	public List<String> getProductImages(Integer productId) {

		List<ProductImage> productImages = productImageRepository.findByProduct_ProductId(productId);
		List<String> imageUrls = new ArrayList<>();
		
		for(ProductImage image: productImages) {
			imageUrls.add(image.getImageUrl());
		}
		return imageUrls;
	}

}
