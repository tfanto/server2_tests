package com.fnt.model;

import java.time.LocalDateTime;

public class CustomerOrderLine {

	private CustomerOrderLinePK primaryKey;

	private LocalDateTime date;

	private String itemId;

	private Integer numberOfItems;

	private Double pricePerItem;

	public CustomerOrderLinePK getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(CustomerOrderLinePK primaryKey) {
		this.primaryKey = primaryKey;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public Integer getNumberOfItems() {
		return numberOfItems;
	}

	public void setNumberOfItems(Integer numberOfItems) {
		this.numberOfItems = numberOfItems;
	}

	public Double getPricePerItem() {
		return pricePerItem;
	}

	public void setPricePerItem(Double pricePerItem) {
		this.pricePerItem = pricePerItem;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((itemId == null) ? 0 : itemId.hashCode());
		result = prime * result + ((numberOfItems == null) ? 0 : numberOfItems.hashCode());
		result = prime * result + ((pricePerItem == null) ? 0 : pricePerItem.hashCode());
		result = prime * result + ((primaryKey == null) ? 0 : primaryKey.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CustomerOrderLine other = (CustomerOrderLine) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (itemId == null) {
			if (other.itemId != null)
				return false;
		} else if (!itemId.equals(other.itemId))
			return false;
		if (numberOfItems == null) {
			if (other.numberOfItems != null)
				return false;
		} else if (!numberOfItems.equals(other.numberOfItems))
			return false;
		if (pricePerItem == null) {
			if (other.pricePerItem != null)
				return false;
		} else if (!pricePerItem.equals(other.pricePerItem))
			return false;
		if (primaryKey == null) {
			if (other.primaryKey != null)
				return false;
		} else if (!primaryKey.equals(other.primaryKey))
			return false;
		return true;
	}

}
