package com.reminder.penshop.admin.model.dto;

import com.reminder.penshop.member.model.dto.MemberDTO;

import lombok.Data;

@Data
public class MemberSuspDTO {
	
	private MemberDTO member;
	private SuspDTO susp;

}
