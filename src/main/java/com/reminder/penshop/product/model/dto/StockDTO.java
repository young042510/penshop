package com.reminder.penshop.product.model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class StockDTO {

    private int stockNo;
    private int stockAmount;
    private Date stockDate;
}