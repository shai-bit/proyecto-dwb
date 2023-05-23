package com.product.api.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "product")
public class DtoProductList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("product_id")
    @Column(name = "product_id")
    private Integer product_id;

	@JsonProperty("gtin")
	@Column(name = "gtin")
	@NotNull(message="gtin is required")
	private String gtin;
	
	@JsonProperty("product")
	@Column(name = "product")
	@NotNull(message="product is required")
	private String product;
    
	@JsonProperty("price")
	@Column(name = "price")
	@NotNull(message="price is required")
	@Min(value=0, message="price must be positive")
	private Double price;

    public Integer getProduct_id() {
        return this.product_id;
    }

    public void setProduct_id(Integer product_id) {
        this.product_id = product_id;
    }

    public String getGtin() {
        return this.gtin;
    }

    public void setGtin(String gtin) {
        this.gtin = gtin;
    }

    public String getProduct() {
        return this.product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Double getPrice() {
        return this.price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

}
