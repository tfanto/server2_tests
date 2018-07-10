package com.fnt.model;

import java.util.ArrayList;
import java.util.List;


public class CustomerOrder {

	private CustomerOrderHead head = null;
	private List<CustomerOrderLine> lines = new ArrayList<>();

	public CustomerOrder() {
	}

	public CustomerOrderHead getHead() {
		return head;
	}

	public void setHead(CustomerOrderHead head) {
		this.head = head;
	}

	public List<CustomerOrderLine> getLines() {
		return lines;
	}

	public void setLines(List<CustomerOrderLine> lines) {
		this.lines = lines;
	}

}
