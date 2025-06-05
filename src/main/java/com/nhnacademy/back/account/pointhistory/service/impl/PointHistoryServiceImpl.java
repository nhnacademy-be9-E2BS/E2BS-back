package com.nhnacademy.back.account.pointhistory.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.exception.NotFoundMemberException;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.account.pointhistory.domain.dto.response.ResponseMemberPointDTO;
import com.nhnacademy.back.account.pointhistory.domain.dto.response.ResponsePointHistoryDTO;
import com.nhnacademy.back.account.pointhistory.domain.entity.PointHistory;
import com.nhnacademy.back.account.pointhistory.exception.InsufficientPointException;
import com.nhnacademy.back.account.pointhistory.repository.PointHistoryJpaRepository;
import com.nhnacademy.back.account.pointhistory.service.PointHistoryService;
import com.nhnacademy.back.pointpolicy.domain.entity.PointPolicy;
import com.nhnacademy.back.pointpolicy.domain.entity.PointPolicyType;
import com.nhnacademy.back.pointpolicy.exception.PointPolicyNotFoundException;
import com.nhnacademy.back.pointpolicy.repository.PointPolicyJpaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PointHistoryServiceImpl implements PointHistoryService {

	private final PointHistoryJpaRepository pointHistoryJpaRepository;
	private final MemberJpaRepository memberJpaRepository;
	private final PointPolicyJpaRepository pointPolicyJpaRepository;

	private static final String MEMBER_NOT_FOUND_EXCEPTION = "아이디에 해당하는 회원을 찾지 못했습니다.";
	private static final String REGISTER_POINT_REASON = "회원가입";
	private static final String REVIEW_IMG_POINT_REASON = "이미지 리뷰 작성";
	private static final String REVIEW_POINT_REASON = "일반 리뷰 작성";
	private static final String BOOK_POINT_REASON = "도서 구매 적립";
	private static final String PAY_POINT_REASON = "포인트 사용";
	private static final String RECOVER_POINT_REASON = "주문취소로 인한 포인트 복구";
	private static final String RETRIEVE_POINT_REASON = "주문취소로 인한 포인트 회수";

	public ResponseMemberPointDTO getMemberPoints(String memberId) {
		Member member = memberJpaRepository.getMemberByMemberId(memberId);
		if (Objects.isNull(member)) {
			throw new NotFoundMemberException(MEMBER_NOT_FOUND_EXCEPTION);
		}

		List<PointHistory> pointHistories = pointHistoryJpaRepository.getPointHistoriesByMember(member);

		long pointAmount = 0;
		for (PointHistory pointHistory : pointHistories) {
			pointAmount += pointHistory.getPointAmount();
		}

		return new ResponseMemberPointDTO(memberId, pointAmount);
	}

	@Override
	public Page<ResponsePointHistoryDTO> getPointHistoryByMemberId(String memberId, Pageable pageable) {
		Member member = memberJpaRepository.getMemberByMemberId(memberId);
		if (Objects.isNull(member)) {
			throw new NotFoundMemberException(MEMBER_NOT_FOUND_EXCEPTION);
		}

		return pointHistoryJpaRepository.findByMemberOrderByPointCreatedAtDesc(member, pageable)
			.map(pointHistory -> new ResponsePointHistoryDTO(
				pointHistory.getPointAmount(),
				pointHistory.getPointReason(),
				pointHistory.getPointCreatedAt()
			));
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW) // 전파 옵션 설정 : 무조건 새로운 트랜잭션 시작 (기존 트랜잭션 일시 중단)
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

		log.info("회원가입 포인트 적립 시작");

		PointHistory pointHistory = new PointHistory(
			pointPolicy.getPointPolicyFigure(),
			REGISTER_POINT_REASON,
			LocalDateTime.now(),
			member
		);

		pointHistoryJpaRepository.save(pointHistory);
		log.info("회원가입 포인트 적립 성공");
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
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
			REVIEW_IMG_POINT_REASON,
			LocalDateTime.now(),
			member
		);

		pointHistoryJpaRepository.save(pointHistory);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
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
			REVIEW_POINT_REASON,
			LocalDateTime.now(),
			member
		);

		pointHistoryJpaRepository.save(pointHistory);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void earnOrderPoint(Long customerId, Long pointFigure) {
		Member member = memberJpaRepository.getMemberByCustomerId(customerId);
		if (Objects.isNull(member)) {
			throw new NotFoundMemberException(MEMBER_NOT_FOUND_EXCEPTION);
		}

		PointHistory pointHistory = new PointHistory(
			pointFigure,
			BOOK_POINT_REASON,
			LocalDateTime.now(),
			member
		);

		pointHistoryJpaRepository.save(pointHistory);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void payPoint(Long customerId, Long pointFigure) {
		Member member = memberJpaRepository.getMemberByCustomerId(customerId);
		if (Objects.isNull(member)) {
			throw new NotFoundMemberException(MEMBER_NOT_FOUND_EXCEPTION);
		}

		ResponseMemberPointDTO points = this.getMemberPoints(member.getMemberId());
		if(points.getPointAmount() < pointFigure) {
			throw new InsufficientPointException("사용가능한 포인트가 부족합니다.");
		}

		PointHistory pointHistory = new PointHistory(
			pointFigure,
			PAY_POINT_REASON,
			LocalDateTime.now(),
			member
		);

		pointHistoryJpaRepository.save(pointHistory);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void recoverPoint(Long customerId, Long pointFigure) {
		Member member = memberJpaRepository.getMemberByCustomerId(customerId);
		if (Objects.isNull(member)) {
			throw new NotFoundMemberException(MEMBER_NOT_FOUND_EXCEPTION);
		}

		PointHistory pointHistory = new PointHistory(
			pointFigure,
			RECOVER_POINT_REASON,
			LocalDateTime.now(),
			member
		);

		pointHistoryJpaRepository.save(pointHistory);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void retrievePoint(Long customerId, Long pointFigure) {
		Member member = memberJpaRepository.getMemberByCustomerId(customerId);
		if (Objects.isNull(member)) {
			throw new NotFoundMemberException(MEMBER_NOT_FOUND_EXCEPTION);
		}

		PointHistory pointHistory = new PointHistory(
			pointFigure,
			RETRIEVE_POINT_REASON,
			LocalDateTime.now(),
			member
		);

		pointHistoryJpaRepository.save(pointHistory);
	}
}
