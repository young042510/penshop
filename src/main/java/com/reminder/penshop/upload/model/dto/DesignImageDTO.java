package com.reminder.penshop.upload.model.dto;

import com.reminder.penshop.product.model.dto.ProductDTO;

import lombok.Data;

@Data
public class DesignImageDTO {

	private int designImageNo;
	private int refProdNo;
	private String origImageName;
	private String saveImageName;
	private String savePath;
	private String imageType;
	private char displayStatusYn;
	private ProductDTO product;
}
