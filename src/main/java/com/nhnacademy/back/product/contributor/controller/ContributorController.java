package com.nhnacademy.back.product.contributor.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.common.annotation.Admin;
import com.nhnacademy.back.common.exception.ValidationFailedException;
import com.nhnacademy.back.product.contributor.domain.dto.request.RequestContributorDTO;
import com.nhnacademy.back.product.contributor.domain.dto.response.ResponseContributorDTO;
import com.nhnacademy.back.product.contributor.service.ContributorService;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "기여자(관리자)", description = "관리자 기여자 관련 API")
@RestController
@RequestMapping("/api/admin/contributors")
public class ContributorController {
	private final ContributorService contributorService;

	public ContributorController(ContributorService contributorService) {
		this.contributorService = contributorService;
	}

	/**
	 * 기여자 목록 전체 조회
	 */
	@Admin
	@GetMapping()
	public ResponseEntity<Page<ResponseContributorDTO>> getContributors(@PageableDefault() Pageable pageable) {
		Page<ResponseContributorDTO> contributors = contributorService.getContributors(pageable);
		return ResponseEntity.status(HttpStatus.OK).body(contributors);
	}

	/**
	 * 기여자 id로 기여자 조회
	 */
	@Admin
	@GetMapping("/{contributorId}")
	public ResponseEntity<ResponseContributorDTO> getContributor(@PathVariable Long contributorId) {
		ResponseContributorDTO responseContributorDTO = contributorService.getContributor(contributorId);
		return ResponseEntity.status(HttpStatus.OK).body(responseContributorDTO);
	}

	/**
	 * 기여자 생성
	 */
	@Admin
	@PostMapping()
	public ResponseEntity<Void> createContributor(@Validated @RequestBody RequestContributorDTO request,
		BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}
		contributorService.createContributor(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 기여자 수정
	 */
	@Admin
	@PutMapping("/{contributorId}")
	public ResponseEntity<ResponseContributorDTO> updateContributor(
		@Validated @RequestBody RequestContributorDTO request, @PathVariable Long contributorId,
		BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}
		ResponseContributorDTO response = contributorService.updateContributor(contributorId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

}
