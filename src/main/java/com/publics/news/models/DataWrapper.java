package com.publics.news.models;

import java.util.List;

import lombok.Data;

@Data
public class DataWrapper {

	List<ProductDataWrapper> products;

	List<String> unit;

}
