package com.nhnacademy.back.home.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.exception.NotFoundMemberException;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.home.dto.response.ResponseHomeMemberNameDTO;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HomeService {

	private final MemberJpaRepository memberJpaRepository;

	public ResponseHomeMemberNameDTO getHomeMemberName(String memberId) {
		Member member = memberJpaRepository.getMemberByMemberId(memberId);
		if (Objects.isNull(member)) {
			throw new NotFoundMemberException("아이디에 해당하는 회원을 찾지 못했습니다.");
		}

		return new ResponseHomeMemberNameDTO(
			memberId, member.getCustomer().getCustomerName()
		);
	}

}
