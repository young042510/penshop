package com.reminder.penshop.cart.model.dto;


import lombok.Data;

import java.sql.Date;


@Data
public class DeliveryDTO {

    private String orderNo;
    private int deliveryFee;
    private String deliveryCompany;
    private Date dispatchDate;
    private Date deliveryDate;
}
