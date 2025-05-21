package com.nhnacademy.back.account.pointhistory.service.impl;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.exception.NotFoundMemberException;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.account.pointhistory.domain.dto.response.ResponseMemberPointDTO;
import com.nhnacademy.back.account.pointhistory.domain.entity.PointHistory;
import com.nhnacademy.back.account.pointhistory.repository.PointHistoryJpaRepository;
import com.nhnacademy.back.account.pointhistory.service.PointHistoryService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PointHistoryServiceImpl implements PointHistoryService {

	private final PointHistoryJpaRepository pointHistoryJpaRepository;
	private final MemberJpaRepository memberJpaRepository;

	@Transactional
	public ResponseMemberPointDTO getMemberPoints(String memberId) {
		Member member = memberJpaRepository.getMemberByMemberId(memberId);
		if (Objects.isNull(member)) {
			throw new NotFoundMemberException("아이디에 해당하는 회원을 찾지 못했습니다.");
		}

		List<PointHistory> pointHistories = pointHistoryJpaRepository.getPointHistoriesByMember(member);

		long pointAmount = 0;
		for (int i = 0; i < pointHistories.size(); i++) {
			pointAmount += pointHistories.get(i).getPointAmount();
		}

		return new ResponseMemberPointDTO(memberId, pointAmount);
	}

}
