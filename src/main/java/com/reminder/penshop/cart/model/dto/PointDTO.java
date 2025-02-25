package com.reminder.penshop.cart.model.dto;

import lombok.Data;

@Data
public class PointDTO {

	private int pointNo;
	private String paymentNo;
	private String bonusReason;
	private int pointAmount;
	private String pointDateTime;
	private String pointStatus;
	private PaymentDTO payment;
}
