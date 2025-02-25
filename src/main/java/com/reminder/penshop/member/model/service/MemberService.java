package com.reminder.penshop.member.model.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.reminder.penshop.cart.model.dto.OrderDTO;
import com.reminder.penshop.cart.model.dto.OrderDetailDTO;
import com.reminder.penshop.cart.model.dto.PointDTO;
import com.reminder.penshop.member.model.dto.MemberDTO;
import com.reminder.penshop.member.model.dto.WishListDTO;
import com.reminder.penshop.review.model.dto.ReviewDTO;

/**
 * Spring Security 모듈 UserDetailsService 상속 받아 로그인/로그아웃 로직 처리
 */
public interface MemberService extends UserDetailsService { 
	
	/* 회원가입 */
	int checkId(String memberId);
	
	int checkEmail(String email);
	
	boolean signUpMember(MemberDTO member) throws Exception;
	
	int activateAccountByEmail(String email);
	
	/* 로그인 */
	void updateAccumLoginCount(String username);
	
	void updateLatestLoginDate(String username);
	
	void resetLoginFailedCount(String username);
	
	void updateLoginFailedCount(String username);

	int checkLoginFailedCount(String username);
	
	/* 관리자페이지 */
	void deactivateUsername(String username);

	/* 마이페이지 */
	void addToWishList(String memberId, int optionNo);

	List<WishListDTO> getMemberWishList(String memberId);
	
	List<Integer> getProdNoFromWishList(String memberId);

	int deleteItemFromWishList(String memberId, int optionNo);

	List<OrderDetailDTO> getMemberOrderList(String memberId);

	List<PointDTO> getReserveDetails(String memberId);

	OrderDetailDTO getMemberOrderDetails(String memberId, String orderNo);

	List<OrderDTO> getOptionListByOrderNo(String orderNo);

	int getTotalOrderAmountByOrderNo(String orderNo);

	List<OrderDTO> getItemsToPostAReview(String memberId);

	OrderDTO getOrderInfoToReview(String memberId, String orderNo, int optionNo);

	List<ReviewDTO> getMemberReviewPosts(String memberId);

	int getMemberPoint(String memberId);
	
	int getMemberOrderCountByDlvrStatus(String memberId, String dlvrStatus);

	MemberDTO getMemberDetails(String memberId);

	char checkIsAuthenticated(String memberId);

	boolean updateAuthentication(String memberId, char authPhoneYn);

	int changeMemberInfo(MemberDTO memberDTO);
	
	int closeMemberAccount(String memberId);
	
	/* 아이디/비밀번호 찾기 */
	MemberDTO findMemberId(String name, String email);

	int generateTempPwd(MemberDTO memberDTO);

	MemberDTO findMemberByEmail(String email);

	Integer checkAdminOrNot(String memberId);
}
