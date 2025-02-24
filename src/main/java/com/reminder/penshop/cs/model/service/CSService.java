package com.reminder.penshop.cs.model.service;

import java.util.List;

import com.reminder.penshop.cs.model.dto.InquiryDTO;
import com.reminder.penshop.paging.model.dto.Criteria;
import com.reminder.penshop.upload.model.dto.AttachmentDTO;

public interface CSService {

	int writeAInquiry(InquiryDTO inquiryDTO);
	
	int getTotalInquiryNumber(String memberId, Criteria criteria, String type);

	List<InquiryDTO> getMyInquiryList(String memberId, Criteria criteria, String type);

	int checkCurrInquiryNo();

	int attachInquiryImages(AttachmentDTO tempFileInfo);

	InquiryDTO getInquiryDetails(String memberId, int inquiryNo);

	int updateInquiryAnsweredYn(int refPostNo);

	int incrementInquiryViewCount(int inquiryNo);

	int updateAInquiry(InquiryDTO inquiryDTO);
}
