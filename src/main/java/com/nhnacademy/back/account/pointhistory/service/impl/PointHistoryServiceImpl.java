package com.nhnacademy.back.account.pointhistory.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.exception.NotFoundMemberException;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.account.pointhistory.domain.dto.response.ResponseMemberPointDTO;
import com.nhnacademy.back.account.pointhistory.domain.dto.response.ResponsePointHistoryDTO;
import com.nhnacademy.back.account.pointhistory.domain.entity.PointHistory;
import com.nhnacademy.back.account.pointhistory.repository.PointHistoryJpaRepository;
import com.nhnacademy.back.account.pointhistory.service.PointHistoryService;
import com.nhnacademy.back.pointpolicy.domain.entity.PointPolicy;
import com.nhnacademy.back.pointpolicy.domain.entity.PointPolicyType;
import com.nhnacademy.back.pointpolicy.exception.PointPolicyNotFoundException;
import com.nhnacademy.back.pointpolicy.repository.PointPolicyJpaRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PointHistoryServiceImpl implements PointHistoryService {

	private final PointHistoryJpaRepository pointHistoryJpaRepository;
	private final MemberJpaRepository memberJpaRepository;
	private final PointPolicyJpaRepository pointPolicyJpaRepository;

	private final String MEMBER_NOT_FOUND_EXCEPTION = "아이디에 해당하는 회원을 찾지 못했습니다.";

	public ResponseMemberPointDTO getMemberPoints(String memberId) {
		Member member = memberJpaRepository.getMemberByMemberId(memberId);
		if (Objects.isNull(member)) {
			throw new NotFoundMemberException(MEMBER_NOT_FOUND_EXCEPTION);
		}

		List<PointHistory> pointHistories = pointHistoryJpaRepository.getPointHistoriesByMember(member);

		long pointAmount = 0;
		for (int i = 0; i < pointHistories.size(); i++) {
			pointAmount += pointHistories.get(i).getPointAmount();
		}

		return new ResponseMemberPointDTO(memberId, pointAmount);
	}

	@Override
	public Page<ResponsePointHistoryDTO> getPointHistoryByMemberId(String memberId, Pageable pageable) {
		Member member = memberJpaRepository.getMemberByMemberId(memberId);
		if (Objects.isNull(member)) {
			throw new NotFoundMemberException(MEMBER_NOT_FOUND_EXCEPTION);
		}

		return pointHistoryJpaRepository.getPointHistoriesByMember(member, pageable)
			.map(pointHistory -> new ResponsePointHistoryDTO(
				pointHistory.getPointAmount(),
				pointHistory.getPointReason(),
				pointHistory.getPointCreatedAt()
			));
	}

	@Override
	@Transactional
	public void earnRegisterPoint(String memberId) {
		Member member = memberJpaRepository.getMemberByMemberId(memberId);
		if (Objects.isNull(member)) {
			throw new NotFoundMemberException(MEMBER_NOT_FOUND_EXCEPTION);
		}

		PointPolicy pointPolicy = pointPolicyJpaRepository
			.findByPointPolicyTypeAndPointPolicyIsActive(PointPolicyType.REGISTER, true);
		if(Objects.isNull(pointPolicy)) {
			throw new PointPolicyNotFoundException("활성화된 회원가입 포인트 정책이 없습니다.");
		}

		PointHistory pointHistory = new PointHistory(
			pointPolicy.getPointPolicyFigure(),
			PointPolicyType.REGISTER.getDisplayName(),
			LocalDateTime.now(),
			member
		);

		pointHistoryJpaRepository.save(pointHistory);
	}

	@Override
	@Transactional
	public void earnImgReviewPoint(String memberId) {
		Member member = memberJpaRepository.getMemberByMemberId(memberId);
		if (Objects.isNull(member)) {
			throw new NotFoundMemberException(MEMBER_NOT_FOUND_EXCEPTION);
		}

		PointPolicy pointPolicy = pointPolicyJpaRepository
			.findByPointPolicyTypeAndPointPolicyIsActive(PointPolicyType.REVIEW_IMG, true);
		if(Objects.isNull(pointPolicy)) {
			throw new PointPolicyNotFoundException("활성화된 이미지리뷰 포인트 정책이 없습니다.");
		}

		PointHistory pointHistory = new PointHistory(
			pointPolicy.getPointPolicyFigure(),
			PointPolicyType.REVIEW_IMG.getDisplayName(),
			LocalDateTime.now(),
			member
		);

		pointHistoryJpaRepository.save(pointHistory);
	}

	@Override
	@Transactional
	public void earnReviewPoint(String memberId) {
		Member member = memberJpaRepository.getMemberByMemberId(memberId);
		if (Objects.isNull(member)) {
			throw new NotFoundMemberException(MEMBER_NOT_FOUND_EXCEPTION);
		}

		PointPolicy pointPolicy = pointPolicyJpaRepository
			.findByPointPolicyTypeAndPointPolicyIsActive(PointPolicyType.REVIEW, true);
		if(Objects.isNull(pointPolicy)) {
			throw new PointPolicyNotFoundException("활성화된 일반리뷰 포인트 정책이 없습니다.");
		}

		PointHistory pointHistory = new PointHistory(
			pointPolicy.getPointPolicyFigure(),
			PointPolicyType.REVIEW.getDisplayName(),
			LocalDateTime.now(),
			member
		);

		pointHistoryJpaRepository.save(pointHistory);
	}

	@Override
	@Transactional
	public void earnOrderPoint(String memberId) {
		Member member = memberJpaRepository.getMemberByMemberId(memberId);
		if (Objects.isNull(member)) {
			throw new NotFoundMemberException(MEMBER_NOT_FOUND_EXCEPTION);
		}

		PointPolicy pointPolicy = pointPolicyJpaRepository
			.findByPointPolicyTypeAndPointPolicyIsActive(PointPolicyType.BOOK, true);
		if(Objects.isNull(pointPolicy)) {
			throw new PointPolicyNotFoundException("활성화된 도서주문 포인트 정책이 없습니다.");
		}

		PointHistory pointHistory = new PointHistory(
			pointPolicy.getPointPolicyFigure(),
			PointPolicyType.BOOK.getDisplayName(),
			LocalDateTime.now(),
			member
		);

		pointHistoryJpaRepository.save(pointHistory);
	}
}
