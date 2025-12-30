package com.learnwithmahesh.jwtsecurity.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.learnwithmahesh.jwtsecurity.model.Product;
import com.learnwithmahesh.jwtsecurity.repository.ProductRepository;

@Service
public class ProductService {

	private final ProductRepository productRepository;

	public ProductService(ProductRepository productRepository) {
		super();
		this.productRepository = productRepository;
	}
	

	public List<Product> getAllProducts()
	{
		return productRepository.findAll();
	}
	
	public Optional<Product> getProductById(Long id)
	{
		return productRepository.findById(id);
	}
	
	public Product saveProduct(Product product)
	{
		return productRepository.save(product);
	}
	
	public void deleteProduct(Long id)
	{
		productRepository.deleteById(id);
	}
}
