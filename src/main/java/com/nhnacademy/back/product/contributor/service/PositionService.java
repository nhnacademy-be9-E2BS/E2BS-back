package com.nhnacademy.back.product.contributor.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.back.product.contributor.domain.dto.request.RequestPositionDTO;
import com.nhnacademy.back.product.contributor.domain.dto.response.ResponsePositionDTO;

public interface PositionService {
	/**
	 * 관리자가 역할 생성
	 */
	ResponsePositionDTO createPosition(RequestPositionDTO request);

	/**
	 * 관리자가 아이디로 역할 단건 조회
	 */
	ResponsePositionDTO getPosition(Long positionId);

	/**
	 * 관리자가 역할 전체 조회
	 */
	Page<ResponsePositionDTO> getPositions(Pageable pageable);

	/**
	 * 관리자 역할 수정
	 */
	ResponsePositionDTO updatePosition(Long positionId, RequestPositionDTO request);
}
