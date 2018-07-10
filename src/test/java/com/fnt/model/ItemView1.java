package com.fnt.model;

public class ItemView1 {

	private String id;

	private String description;

	private Integer inStock;

	private Double price;

	public ItemView1() {

	}

	public ItemView1(String id, String description, Integer inStock, Double price) {
		this.id = id;
		this.description = description;
		this.inStock = inStock;
		this.price = price;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getInStock() {
		return inStock;
	}

	public void setInStock(Integer inStock) {
		this.inStock = inStock;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

}
