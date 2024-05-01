package com.publics.news.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDataWrapper {

	private Long id;

	private String item;

	private double length;

	private double width;

	private double thick;

	private int qty;

	private double weight;

	private double area;

	private String finish;

	private String remarks;

	private String side;

	private String type;

}
