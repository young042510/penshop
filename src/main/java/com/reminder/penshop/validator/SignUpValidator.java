package com.reminder.penshop.validator;

import com.reminder.penshop.member.model.dao.MemberMapper;
import com.reminder.penshop.member.model.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@RequiredArgsConstructor
@Component
public class SignUpValidator implements Validator {

    private final MemberMapper memberMapper;

    @Override
    public boolean supports(Class<?> clazz) {
        return MemberDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        MemberDTO member = (MemberDTO) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "memberId", "NotBlank.memberId");

        if(memberMapper.checkId(member.getMemberId()) > 0) {
            errors.rejectValue("memberId", "invalid"); 	//field, errorCode
        }
    }
}
