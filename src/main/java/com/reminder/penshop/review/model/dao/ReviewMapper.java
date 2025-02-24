package com.reminder.penshop.review.model.dao;

import org.apache.ibatis.annotations.Mapper;

import com.reminder.penshop.cart.model.dto.PointDTO;
import com.reminder.penshop.review.model.dto.ReviewDTO;
import com.reminder.penshop.upload.model.dto.AttachmentDTO;

@Mapper
public interface ReviewMapper {

	int checkCurrReviewNo();

	int attachReviewImages(AttachmentDTO attachment);

	int postAReview(ReviewDTO reviewDTO);
	
	String getPaymentNoByOrderNo(String orderNo);

	int savePoints(PointDTO pointDTO);

	int incrementReviewViewCount(int reviewNo);

	String getAttachmentByReviewNo(int reviewNo, int num);
	
	ReviewDTO getReviewDetails(int reviewNo);

	Integer checkReviewNoToEdit(String memberId, String orderNo, int optionNo);

	int updateAReview(ReviewDTO reviewDTO);

	int checkAttachedFiles(int reviewNo);
	
	void deleteAttachedFiles(int reviewNo);

	void deleteDownloadHits(int reviewNo);
}
