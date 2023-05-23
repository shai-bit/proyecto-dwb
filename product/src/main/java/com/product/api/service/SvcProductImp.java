package com.product.api.service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.product.api.dto.ApiResponse;
import com.product.api.dto.DtoProductList;
import com.product.api.entity.Category;
import com.product.api.entity.Product;
import com.product.api.repository.RepoCategory;
import com.product.api.repository.RepoProduct;
import com.product.exception.ApiException;

@Service
public class SvcProductImp implements SvcProduct {

	@Autowired
	RepoProduct repo;
	
	@Autowired
	RepoCategory repoCategory;

	@Override
	public Product getProduct(String gtin) {
		Product product = repo.findByGtinAndStatus(gtin);
		if (product != null) {
			product.setCategory(repoCategory.findByCategoryId(product.getCategory_id()));
			return product;
		}else
			throw new ApiException(HttpStatus.NOT_FOUND, "product does not exist");
	}

	/*
	 * 4. Implementar el método createProduct considerando las siguientes validaciones:
  		1. validar que la categoría del nuevo producto exista
  		2. el código GTIN y el nombre del producto son únicos
  		3. si al intentar realizar un nuevo registro ya existe un producto con el mismo GTIN pero tiene estatus 0, 
  		   entonces se debe cambiar el estatus del producto existente a 1 y actualizar sus datos con los del nuevo registro
	 */
	@Override
	public ApiResponse createProduct(Product in) {
		Product product = repo.findByGtin(in.getGtin());
		if (product != null){
			if (product.getStatus() == 0){
				// repo.activateProduct(in.getGtin());
				repo.activateAndUpdate(product.getProduct_id(), in.getProduct(), in.getDescription(), in.getPrice(), in.getStock());
				return new ApiResponse("product has been activated");
			}
		}
		in.setStatus(1);
		Category category = repoCategory.findByCategoryId(in.getCategory_id());
		if (category == null)
			throw new ApiException(HttpStatus.NOT_FOUND, "category not found");
		try{
			repo.save(in);
		} catch (DataIntegrityViolationException e){
			if (e.getLocalizedMessage().contains("gtin"))
				throw new ApiException(HttpStatus.BAD_REQUEST, "product gtin already exists");
			if (e.getLocalizedMessage().contains("product"))
				throw new ApiException(HttpStatus.BAD_REQUEST, "product name already exists");
		}
		return new ApiResponse("product created");
		
	}

	@Override
	public ApiResponse updateProduct(Product in, Integer id) {
		Integer updated = 0;
		try {
			updated = repo.updateProduct(id, in.getGtin(), in.getProduct(), in.getDescription(), in.getPrice(), in.getStock(), in.getCategory_id());
		}catch (DataIntegrityViolationException e) {
			if (e.getLocalizedMessage().contains("gtin"))
				throw new ApiException(HttpStatus.BAD_REQUEST, "product gtin already exist");
			if (e.getLocalizedMessage().contains("product"))
				throw new ApiException(HttpStatus.BAD_REQUEST, "product name already exist");
			if (e.contains(SQLIntegrityConstraintViolationException.class))
				throw new ApiException(HttpStatus.BAD_REQUEST, "category not found");
		}
		if(updated == 0)
			throw new ApiException(HttpStatus.BAD_REQUEST, "product cannot be updated");
		else
			return new ApiResponse("product updated");
	}

	@Override
	public ApiResponse deleteProduct(Integer id) {
		if (repo.deleteProduct(id) > 0)
			return new ApiResponse("product removed");
		else
			throw new ApiException(HttpStatus.BAD_REQUEST, "product cannot be deleted");
	}

	@Override
	public ApiResponse updateProductStock(String gtin, Integer stock) {
		Product product = getProduct(gtin);
		if(stock > product.getStock())
			throw new ApiException(HttpStatus.BAD_REQUEST, "stock to update is invalid");
		
		repo.updateProductStock(gtin, product.getStock() - stock);
		return new ApiResponse("product stock updated");
	}

	@Override
	public List<DtoProductList> listProducts(Integer category_id) {
		List<Product> productList = repo.findByCategoryId(category_id);
		List<DtoProductList> dtoList = new ArrayList<>();
		for (Product product : productList) {
			DtoProductList dtoProduct = new DtoProductList();
			dtoProduct.setProduct_id(product.getProduct_id());
			dtoProduct.setGtin(product.getGtin());
			dtoProduct.setProduct(product.getProduct());
			dtoProduct.setPrice(product.getPrice());
			dtoList.add(dtoProduct);
		}
		return dtoList;
	}

	@Override
	public ApiResponse updateProductCategory(String gtin, Integer new_id){
		Product product = repo.findByGtin(gtin);
		if (product == null)
			throw new ApiException(HttpStatus.NOT_FOUND, "product does not exist");
		Category category = repoCategory.findByCategoryId(new_id); //checamos si existe la nueva categoría
		if(category == null) 
			throw new ApiException(HttpStatus.NOT_FOUND, "category not found");
		repo.updateProductCategory(gtin, new_id);
		return new ApiResponse("product category updated");
	}
}
