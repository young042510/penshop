package com.reminder.penshop.upload.model.dto;

import com.reminder.penshop.product.model.dto.ProductDTO;

public class DesignImageDTO {

    private int designImageNo;              //디자인이미지번호
    private int refProdNo;                  // 상품번호
    private String origImageName;           // 원본이미지명
    private String saveImageName;           // 저장이미지명
    private String savePath;                // 저장경로
    private String imageType;               // 이미지유형
    private char displayStatusYn;           // 게재여부
    private ProductDTO product;
}
