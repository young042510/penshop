package com.reminder.penshop.member.model.dto;

import lombok.Data;

@Data
public class RoleDTO {
	
	private String memberId;
	private int authorityCode;
	
	private AuthorityDTO authority;
}
