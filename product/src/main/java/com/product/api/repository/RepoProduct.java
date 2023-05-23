package com.product.api.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.product.api.entity.Product;

@Repository
public interface RepoProduct extends JpaRepository<Product, Integer>{
	
	// Método para buscar un producto por su gtin y estatús de 1
	@Query(value = "SELECT p FROM Product p WHERE p.gtin = :gtin AND p.status = 1")
    Product findByGtinAndStatus(@Param("gtin") String gtin);

	// Métodos auxiliares para crear producto en SvcProductImp
	@Query(value = "SELECT p FROM Product p WHERE p.gtin = :gtin")
	Product findByGtin(@Param("gtin") String gtin);

	@Modifying
	@Transactional
	@Query(value = "UPDATE Product SET status = 1 WHERE gtin = :gtin")
	Integer activateProduct(@Param("gtin") String gtin);

	@Modifying
	@Transactional
	@Query(value ="UPDATE product "
						+ "SET product = :product, "
						+ "description = :description, "
						+ "price = :price, "
						+ "stock = :stock, "
						+ "status = 1 "
					+ "WHERE product_id = :product_id", nativeQuery = true)
	Integer activateAndUpdate(
			@Param("product_id") Integer product_id,
			@Param("product") String product, 
			@Param("description") String description, 
			@Param("price") Double price, 
			@Param("stock") Integer stock
		);
	
	@Modifying
	@Transactional
	@Query(value ="UPDATE product "
					+ "SET gtin = :gtin, "
						+ "product = :product, "
						+ "description = :description, "
						+ "price = :price, "
						+ "stock = :stock, "
						+ "status = 1, "
						+ "category_id = :category_id "
					+ "WHERE product_id = :product_id", nativeQuery = true)
	Integer updateProduct(
			@Param("product_id") Integer product_id,
			@Param("gtin") String gtin, 
			@Param("product") String product, 
			@Param("description") String description, 
			@Param("price") Double price, 
			@Param("stock") Integer stock,
			@Param("category_id") Integer category_id
		);
	
	@Modifying
	@Transactional
	@Query(value ="UPDATE product SET status = 0 WHERE product_id = :product_id AND status = 1", nativeQuery = true)
	Integer deleteProduct(@Param("product_id") Integer product_id);
	
	@Modifying
	@Transactional
	@Query(value ="UPDATE product SET stock = :stock WHERE gtin = :gtin AND status = 1", nativeQuery = true)
	Integer updateProductStock(@Param("gtin") String gtin, @Param("stock") Integer stock);

	@Query(value = "SELECT p FROM Product p WHERE p.category_id = :category_id")
	List<Product> findByCategoryId(@Param("category_id") Integer category_id);

	@Modifying
	@Transactional
	@Query(value = "UPDATE product SET category_id = :new_id WHERE gtin = :gtin", nativeQuery = true)
	Integer updateProductCategory(@Param("gtin") String gtin, @Param("new_id") Integer new_id);
}
