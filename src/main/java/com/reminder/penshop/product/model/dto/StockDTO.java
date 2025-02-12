package com.reminder.penshop.product.model.dto;

import lombok.Data;

import java.sql.Date;

@Data
public class StockDTO {

    private int stockNo;
    private int stockAmount;
    private Date stockDate;
}