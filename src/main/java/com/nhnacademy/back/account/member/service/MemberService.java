package com.nhnacademy.back.account.member.service;

import com.nhnacademy.back.account.admin.domain.dto.request.RequestAdminSettingsMemberStateDTO;
import com.nhnacademy.back.account.member.domain.dto.MemberDTO;
import com.nhnacademy.back.account.member.domain.dto.request.RequestLoginMemberDTO;
import com.nhnacademy.back.account.member.domain.dto.request.RequestMemberIdDTO;
import com.nhnacademy.back.account.member.domain.dto.request.RequestMemberInfoDTO;
import com.nhnacademy.back.account.member.domain.dto.request.RequestRegisterMemberDTO;
import com.nhnacademy.back.account.member.domain.dto.response.ResponseMemberEmailDTO;
import com.nhnacademy.back.account.member.domain.dto.response.ResponseMemberInfoDTO;
import com.nhnacademy.back.account.member.domain.dto.response.ResponseMemberStateDTO;
import com.nhnacademy.back.account.member.domain.dto.response.ResponseRegisterMemberDTO;
import com.nhnacademy.back.account.member.domain.entity.Member;

public interface MemberService {

	boolean existsMemberByMemberId(String memberId);

	Member getMemberByMemberId(String memberId);

	MemberDTO loginMember(RequestLoginMemberDTO requestLoginMemberDTO);

	ResponseRegisterMemberDTO registerMember(RequestRegisterMemberDTO requestRegisterMemberDTO);

	ResponseMemberInfoDTO getMemberInfo(RequestMemberIdDTO requestMemberIdDTO);

	void updateMemberInfo(RequestMemberInfoDTO requestMemberInfoDTO);

	void withdrawMember(String memberId);

	int getTotalMemberCnt();

	int getTotalTodayLoginMembersCnt();

	void updateMemberState(String memberId, RequestAdminSettingsMemberStateDTO requestAdminSettingsMemberStateDTO);

	void updateMemberRole(String memberId);

	ResponseMemberStateDTO getMemberState(String memberId);

	String getMemberRole(String memberId);

	void changeDormantMemberStateActive(String memberId);

	ResponseMemberEmailDTO getMemberEmail(String memberId);
}
