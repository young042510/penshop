package com.reminder.penshop.review.model.service;

import com.reminder.penshop.cart.model.dto.PointDTO;
import com.reminder.penshop.review.model.dto.ReviewDTO;
import com.reminder.penshop.upload.model.dto.AttachmentDTO;

public interface ReviewService {

	int checkCurrReviewNo();

	int attachReviewImages(AttachmentDTO attachment);

	int postAReview(ReviewDTO reviewDTO);
	
	String getPaymentNoByOrderNo(String orderNo);

	int savePoints(PointDTO pointDTO);

	int incrementReviewViewCount(int reviewNo);

	String getAttachmentByReviewNo(int reviewNo, int num);
	
	ReviewDTO getReviewDetails(int reviewNo);

	int checkReviewNoToEdit(String memberId, String orderNo, int optionNo);

	int updateAReview(ReviewDTO reviewDTO);
}
