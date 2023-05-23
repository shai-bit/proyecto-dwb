package com.product.api.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Clase que representa una categoría
 */
@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue
    @Column(name = "category_id")
    private Integer categoryId;

    @NotNull
    @Column(name = "category")
    private String category;

    @NotNull
    @Column(name = "acronym")
    private String acronym;

    @Column(name = "status")
    @Min(value = 0, message = "status must be 0 or 1")
    @Max(value = 1, message = "status must be 0 or 1")
    @JsonIgnore
    private Integer status;

    public Category(){
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public Integer getStatus() {
        return this.status;
    }

    public Integer getCategoryId(){
        return this.categoryId;
    }

    public String getCategory(){
        return category;
    }

    public String getAcronym(){
        return acronym;
    }

    @Override
    public String toString() {
        return "ID:" + categoryId + ", category:" + category + ", acronym:" + acronym + ", status:" + status;
    }
}