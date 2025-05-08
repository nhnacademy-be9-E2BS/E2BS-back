package com.nhnacademy.back.account.member.service;

import com.nhnacademy.back.account.member.domain.dto.request.RequestLoginMemberDTO;
import com.nhnacademy.back.account.member.domain.entity.Member;

public interface MemberService {

	boolean existsMemberByMemberId(String memberId);

	Member getMemberByMemberId(String memberId);

	Member loginMember(RequestLoginMemberDTO requestLoginMemberDTO);

}
