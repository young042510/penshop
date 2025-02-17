package com.reminder.penshop.member.model.service;

import com.reminder.penshop.cart.model.dto.OrderDTO;
import com.reminder.penshop.cart.model.dto.OrderDetailDTO;
import com.reminder.penshop.cart.model.dto.PointDTO;
import com.reminder.penshop.member.model.dao.MemberMapper;
import com.reminder.penshop.member.model.dto.MemberDTO;
import com.reminder.penshop.member.model.dto.RoleDTO;
import com.reminder.penshop.member.model.dto.WishListDTO;
import com.reminder.penshop.review.model.dto.ReviewDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@Transactional
@Service("memberService")
public class MemberServiceImpl implements MemberService {

    private MemberMapper memberMapper;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public MemberServiceImpl(MemberMapper memberMapper, PasswordEncoder passwordEncoder) {
        this.memberMapper = memberMapper;
        this.passwordEncoder = passwordEncoder;
        /* [PasswordEncoder]
         * 1. pom.xml 통해 spring-boot-starter-security 의존성 주입
         * 2. ContextConfiguration에서 BCryptPasswordEncoder에 대해 Bean 등록
         */
    }

    /*
    * 아이디 중복 검사
    * @return 중복된 아이디 개수
    * */
    @Override
    public int checkId(String memberId) {
        int count = memberMapper.checkId(memberId);
        return count;
    }

    /*
    * 이메일 중복 검사
    * @return 중복된 이메일 개수
    * */

    @Override
    public int checkEmail(String email) {
        int count = memberMapper.checkEmail(email);
        return count;
    }

    /*
    * 회원가입
    * */
    @Override
    public boolean signUpMember(MemberDTO member) throws Exception {

        member.setMemberPwd(passwordEncoder.encode(member.getMemberPwd()));

        /* 1. 회원 등록 */
        int resultA = memberMapper.insertMember(member);

        /* 2. 권한 등록 */
        RoleDTO role = new RoleDTO();
        role.setMemberId(member.getMemberId());
        role.setAuthorityCode(1); // 일반 회원 1
        int resultB = memberMapper.insertRole(role);

        /* 3. 인증여부 등록(기본 N)*/
        int resultC = 0;
        if(resultA <= 0 && resultB <= 0) {
            throw new Exception("신규 회원 등록 실패");
        } else {
            resultC = memberMapper.insertAuthentication(member.getMemberId());
        }

        /* 4. 신규회원 적립금 혜택 등록*/
        PointDTO point = new PointDTO();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd a hh:mm:ss");
        String addDate = simpleDateFormat.format(new Date());
        point.setBonusReason("신규회원가입[" + member.getMemberId() + "]");
        point.setPointAmount(2000);
        point.setPointDateTime(addDate);
        point.setPointStatus("적립");
        int resultD = memberMapper.addNewMemberBonusPoints(point);

        return resultA > 0 && resultB > 0 && resultC > 0 && resultD > 0 ? true : false;
    }

    @Override
    public int activateAccountByEmail(String email) {
        return 0;
    }

    @Override
    public void updateAccumLoginCount(String username) {

    }

    @Override
    public void updateLatestLoginDate(String username) {

    }

    @Override
    public void resetLoginFailedCount(String username) {

    }

    @Override
    public void updateLoginFailedCount(String username) {

    }

    @Override
    public int checkLoginFailedCount(String username) {
        return 0;
    }

    @Override
    public void deactivateUsername(String username) {

    }

    @Override
    public void addToWishList(String memberId, int optionNo) {

    }

    @Override
    public List<WishListDTO> getMemberWishList(String memberId) {
        return List.of();
    }

    @Override
    public List<Integer> getProdNoFromWishList(String memberId) {
        return List.of();
    }

    @Override
    public int deleteItemFromWishList(String memberId, int optionNo) {
        return 0;
    }

    @Override
    public List<OrderDetailDTO> getMemberOrderList(String memberId) {
        return List.of();
    }

    @Override
    public List<PointDTO> getReserveDetails(String memberId) {
        return List.of();
    }

    @Override
    public OrderDetailDTO getMemberOrderDetails(String memberId, String orderNo) {
        return null;
    }

    @Override
    public List<OrderDTO> getOptionListByOrderNo(String orderNo) {
        return List.of();
    }

    @Override
    public int getTotalOrderAmountByOrderNo(String orderNo) {
        return 0;
    }

    @Override
    public List<OrderDTO> getItemsToPostAReview(String memberId) {
        return List.of();
    }

    @Override
    public OrderDTO getOrderInfoToReview(String memberId, String orderNo, int optionNo) {
        return null;
    }

    @Override
    public List<ReviewDTO> getMemberReviewPosts(String memberId) {
        return List.of();
    }

    @Override
    public int getMemberPoint(String memberId) {
        return 0;
    }

    @Override
    public int getMemberOrderCountByDlvrStatus(String memberId, String dlvrStatus) {
        return 0;
    }

    @Override
    public MemberDTO getMemberDetails(String memberId) {
        return null;
    }

    @Override
    public char checkIsAuthenticated(String memberId) {
        return 0;
    }

    @Override
    public boolean updateAuthentication(String memberId, char authPhoneYn) {
        return false;
    }

    @Override
    public int changeMemberInfo(MemberDTO memberDTO) {
        return 0;
    }

    @Override
    public int closeMemberAccount(String memberId) {
        return 0;
    }

    @Override
    public MemberDTO findMemberId(String name, String email) {
        return null;
    }

    @Override
    public int generateTempPwd(MemberDTO memberDTO) {
        return 0;
    }

    @Override
    public MemberDTO findMemberByEmail(String email) {
        return null;
    }

    @Override
    public Integer checkAdminOrNot(String memberId) {
        return 0;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}