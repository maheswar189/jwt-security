package com.learnwithmahesh.jwtsecurity.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.learnwithmahesh.jwtsecurity.model.Product;
import com.learnwithmahesh.jwtsecurity.service.ProductService;

@RestController
@RequestMapping("/api/")
public class ProductController {

	private final ProductService productService;

	public ProductController(ProductService productService) {
		super();
		this.productService = productService;
	}
	
	@GetMapping("/products")
	public ResponseEntity<?> getAllProducts()
	{
		List<Product> allProducts = productService.getAllProducts();
		System.out.println("allProducts::"+allProducts);
		return ResponseEntity.ok(allProducts);
	}
	@GetMapping("/{id}")
	public ResponseEntity<?> getProductById(@PathVariable Long id)
	{
		return ResponseEntity.ok(productService.getProductById(id));
	}
	
	@PostMapping("/save")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> saveProduct(@RequestBody Product product)
	{
		return ResponseEntity.ok(productService.saveProduct(product));
	}
	
	@PutMapping("/update/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product product)
	{
		return ResponseEntity.ok(productService.saveProduct(product));
	}
	
	@DeleteMapping("/delete/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> deleteProduct(@PathVariable Long id)
	{
		productService.deleteProduct(id);
		return ResponseEntity.ok("Product deleted Successfully");
	}
}
