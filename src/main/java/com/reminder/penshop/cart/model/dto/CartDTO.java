package com.reminder.penshop.cart.model.dto;

import com.reminder.penshop.product.model.dto.BrandDTO;
import com.reminder.penshop.product.model.dto.CategoryDTO;
import com.reminder.penshop.product.model.dto.OptionDTO;
import com.reminder.penshop.product.model.dto.ProductDTO;
import com.reminder.penshop.upload.model.dto.AttachmentDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {

    private int cartNo;
    private String memberId;
    private int prodNo;
    private int optionNo;
    private int quantity;
    private ProductDTO product;
    private OptionDTO option;
    private CategoryDTO category;
    private BrandDTO brand;
    private List<AttachmentDTO> attachmentList;
}
