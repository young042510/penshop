package com.reminder.penshop.admin.model.dto;

import java.util.Date;
import com.reminder.penshop.member.model.dto.MemberDTO;
import lombok.Data;

@Data
public class SuspDTO {
	
	private int suspNo;
	private MemberDTO memberId;
	private int accSuspCount;
	private String accSuspDesc;
	private Date accSuspDate;
}
