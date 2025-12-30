package com.learnwithmahesh.jwtsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learnwithmahesh.jwtsecurity.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{

}
