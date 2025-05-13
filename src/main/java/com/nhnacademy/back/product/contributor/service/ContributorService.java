package com.nhnacademy.back.product.contributor.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.back.product.contributor.domain.dto.request.RequestContributorDTO;
import com.nhnacademy.back.product.contributor.domain.dto.response.ResponseContributorDTO;
import com.nhnacademy.back.product.contributor.domain.entity.Contributor;

public interface ContributorService {
	/**
	 * 기여자 생성
	 */
	void createContributor(RequestContributorDTO request);

	/**
	 * id로 기여자 조회
	 */
	ResponseContributorDTO getContributor(long contributorId);

	/**
	 * 기여자 목록 전체 조회 (페이징 처리)
	 */
	Page<ResponseContributorDTO> getContributors(Pageable pageable);

	/**
	 * 기여자 수정
	 */
	ResponseContributorDTO updateContributor(long contributorId, RequestContributorDTO request);


}
