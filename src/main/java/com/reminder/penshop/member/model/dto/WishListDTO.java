package com.reminder.penshop.member.model.dto;

import java.util.List;

import com.reminder.penshop.product.model.dto.BrandDTO;
import com.reminder.penshop.product.model.dto.CategoryDTO;
import com.reminder.penshop.product.model.dto.OptionDTO;
import com.reminder.penshop.product.model.dto.ProductDTO;
import com.reminder.penshop.upload.model.dto.AttachmentDTO;

import lombok.Data;

@Data
public class WishListDTO {

	private String memberId;
	private int optionNo;
	private ProductDTO product;
	private OptionDTO option;
	private CategoryDTO category;
	private BrandDTO brand;
	private List<AttachmentDTO> attachmentList;
}
