package com.reminder.penshop.cs.model.dto;

import java.util.Date;
import java.util.List;

import com.reminder.penshop.member.model.dto.MemberDTO;
import com.reminder.penshop.upload.model.dto.AttachmentDTO;

import lombok.Data;

@Data
public class InquiryDTO {

	private int inquiryNo;
	private String memberId;
	private String inquiryTitle;
	private String inquiryContent;
	private String inquiryType; //enum
	private Date inquiryRegDate;
	private Date inquiryChgDate;
	private int inquiryHits;
	private char inquiryAnsweredYn;
	private MemberDTO member;
	private List<AttachmentDTO> attachmentList;
}
