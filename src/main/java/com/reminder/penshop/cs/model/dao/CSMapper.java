package com.reminder.penshop.cs.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.reminder.penshop.cs.model.dto.InquiryDTO;
import com.reminder.penshop.paging.model.dto.Criteria;
import com.reminder.penshop.upload.model.dto.AttachmentDTO;

import io.lettuce.core.dynamic.annotation.Param;

@Mapper
public interface CSMapper {

	int writeAInquiry(InquiryDTO inquiryDTO);
	
	int getTotalInquiryNumber(String memberId, @Param("criteria") Criteria criteria, String type);

	List<InquiryDTO> getMyInquiryList(String memberId, @Param("criteria") Criteria criteria, String type); //@Param 적용 후 mapper에서 parameterType="map" 정의 필요

	int checkCurrInquiryNo();

	int attachInquiryImages(AttachmentDTO tempFileInfo);

	InquiryDTO getInquiryDetails(String memberId, int inquiryNo);

	int updateInquiryAnsweredYn(int refPostNo);

	int incrementInquiryViewCount(int inquiryNo);

	int updateAInquiry(InquiryDTO inquiryDTO);

	int checkAttachedFiles(int inquiryNo);

	void deleteAttachedFiles(int inquiryNo);

	void deleteDownloadHits(int inquiryNo);
}
