package com.nhnacademy.back.account.member.service;

import com.nhnacademy.back.account.member.domain.dto.MemberDTO;
import com.nhnacademy.back.account.member.domain.dto.request.RequestLoginMemberDTO;
import com.nhnacademy.back.account.member.domain.dto.request.RequestRegisterMemberDTO;
import com.nhnacademy.back.account.member.domain.dto.response.ResponseRegisterMemberDTO;
import com.nhnacademy.back.account.member.domain.entity.Member;

public interface MemberService {

	boolean existsMemberByMemberId(String memberId);

	Member getMemberByMemberId(String memberId);

	MemberDTO loginMember(RequestLoginMemberDTO requestLoginMemberDTO);

	ResponseRegisterMemberDTO registerMember(RequestRegisterMemberDTO requestRegisterMemberDTO);

}
