package com.reminder.penshop.member.model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class AuthenticationDTO {

    private String memberId;
    private char authPhoneYn;
    private Date authDate;
}

