package com.reminder.penshop.cart.model.dto;

import lombok.Data;

@Data
public class PaymentDTO {

	private String paymentNo;
	private String orderNo;
	private String paymentMethod;
	private int paymentAmount;
	private String paymentDateTime;
}
