package com.reminder.penshop.cart.model.dto;

import java.util.List;

import com.reminder.penshop.product.model.dto.BrandDTO;
import com.reminder.penshop.product.model.dto.OptionDTO;
import com.reminder.penshop.product.model.dto.ProductDTO;
import com.reminder.penshop.upload.model.dto.AttachmentDTO;

import lombok.Data;

@Data
public class OrderDTO {

	private String orderNo;
	private int optionNo;
	private int orderQuantity;
	private int orderAmount;
	private OrderDetailDTO orderDetail;
	private DeliveryDTO delivery;
	private OptionDTO option;
	private ProductDTO product;
	private BrandDTO brand;
	private List<AttachmentDTO> attachmentList;
}
