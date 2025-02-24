package com.reminder.penshop.mail.model.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MailDTO {

	private String to;
	private String subject;
	private String text;
}
