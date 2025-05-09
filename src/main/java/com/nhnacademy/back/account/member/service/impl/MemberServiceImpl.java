package com.nhnacademy.back.account.member.service.impl;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.nhnacademy.back.account.member.domain.dto.request.RequestLoginMemberDTO;
import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.exception.LoginMemberIsNotExistsException;
import com.nhnacademy.back.account.member.exception.NotFoundMemberException;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.account.member.service.MemberService;
import com.nhnacademy.back.common.exception.BadRequestException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

	private final MemberJpaRepository memberJpaRepository;

	/**
	 * 아이디에 해당하는 Member가 존재하는지 확인하는 메서드
	 */
	@Override
	public boolean existsMemberByMemberId(String memberId) {
		return memberJpaRepository.existsMemberByMemberId(memberId);
	}

	@Override
	public Member getMemberByMemberId(String memberId) {
		return memberJpaRepository.getMemberByMemberId(memberId);
	}

	@Override
	public Member loginMember(RequestLoginMemberDTO requestLoginMemberDTO) {
		if(Objects.isNull(requestLoginMemberDTO) || Objects.isNull(requestLoginMemberDTO.getMemberId())) {
			throw new BadRequestException("로그인 요청 DTO를 받지 못했습니다.");
		}

		if(!existsMemberByMemberId(requestLoginMemberDTO.getMemberId())) {
			throw new LoginMemberIsNotExistsException("아이디에 해당하는 회원이 존재하지 않습니다.");
		}

		Member member = getMemberByMemberId(requestLoginMemberDTO.getMemberId());
		if(Objects.isNull(member)) {
			throw new NotFoundMemberException("아이디에 해당하는 회원을 찾지 못했습니다.");
		}

		return member;
	}

}
