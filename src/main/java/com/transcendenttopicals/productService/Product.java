package com.transcendenttopicals.productService;

import java.math.BigDecimal;


public class Product {
	
	private String productId;
	private String productName;
	private String description;
	private BigDecimal price;
	private int inventory;
	
	public Product(String productId, String productName, String description, BigDecimal price, int inventory) {
		super();
		this.productId = productId;
		this.productName = productName;
		this.description = description;
		this.price = price;
		this.inventory = inventory;
	}
	
	
	
	public Product() {
		
	}
	
	
	

	public int getInventory() {
		return inventory;
	}



	public void setInventory(int inventory) {
		this.inventory = inventory;
	}



	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	
	
}
