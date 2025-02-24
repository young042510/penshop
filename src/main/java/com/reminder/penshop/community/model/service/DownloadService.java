package com.reminder.penshop.community.model.service;

import com.reminder.penshop.upload.model.dto.AttachmentDTO;

public interface DownloadService {

	AttachmentDTO getAttachmentFile(int attachmentNo);

	int checkCurrAttachNo();

	int setDownloadCount(int currAttachNo);
	
	int incrementDownloadCount(int attachmentNo);
}
