package com.nhnacademy.back.account.pointhistory.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.back.account.pointhistory.domain.dto.response.ResponseMemberPointDTO;
import com.nhnacademy.back.account.pointhistory.domain.dto.response.ResponsePointHistoryDTO;

public interface PointHistoryService {

	/**
	 * 사용가능 포인트 조회
	 */
	ResponseMemberPointDTO getMemberPoints(String memberId);

	/**
	 * 포인트 내역 조회
	 */
	Page<ResponsePointHistoryDTO> getPointHistoryByMemberId(String memberId, Pageable pageable);

	/**
	 * 회원가입, 이미지리뷰 작성, 일반리뷰 작성, 주문 시 포인트 적립
	 */
	void earnRegisterPoint(String memberId);

	void earnImgReviewPoint(String memberId);

	void earnReviewPoint(String memberId);

	void earnOrderPoint(String memberId, Long pointFigure);

	/**
	 * 포인트 사용
	 */
	void payPoint(String memberId, Long pointFigure);

	/**
	 * 주문 취소로 인한 포인트 복구
	 */
	void recoverPoint(String memberId, Long pointFigure);

	/**
	 * 주문 취소로 인한 포인트 회수
	 */
	void retrievePoint(String memberId, Long pointFigure);
}
