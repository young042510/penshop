package com.reminder.penshop.cart.model.dto;

import java.util.List;

import com.reminder.penshop.member.model.dto.MemberDTO;
import com.reminder.penshop.product.model.dto.BrandDTO;
import com.reminder.penshop.product.model.dto.OptionDTO;
import com.reminder.penshop.product.model.dto.ProductDTO;
import com.reminder.penshop.upload.model.dto.AttachmentDTO;

import lombok.Data;

@Data
public class OrderDetailDTO {

	private String orderNo;
	private String memberId;
	private String orderDate;
	private String rcvrName;
	private String rcvrPhone;
	private String rcvrAddress;
	private String dlvrReqMessage;
	private String dlvrStatus;
	private OrderDTO order;
	private PaymentDTO payment;
	private DeliveryDTO delivery;
	private PointDTO point;
	private OptionDTO option;
	private ProductDTO product;
	private BrandDTO brand;
	private List<AttachmentDTO> attachmentList;
	private MemberDTO member;
}
