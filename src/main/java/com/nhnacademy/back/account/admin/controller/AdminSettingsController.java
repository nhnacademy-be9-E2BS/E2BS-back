package com.nhnacademy.back.account.admin.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nhnacademy.back.account.admin.domain.dto.request.RequestAdminSettingsMemberStateDTO;
import com.nhnacademy.back.account.admin.domain.dto.response.ResponseAdminSettingsDTO;
import com.nhnacademy.back.account.admin.domain.dto.response.ResponseAdminSettingsMembersDTO;
import com.nhnacademy.back.account.admin.domain.dto.response.ResponseAdminSettingsNonMembersDTO;
import com.nhnacademy.back.account.admin.service.AdminSettingsService;
import com.nhnacademy.back.account.customer.service.CustomerService;
import com.nhnacademy.back.account.member.service.MemberService;
import com.nhnacademy.back.common.annotation.Admin;
import com.nhnacademy.back.common.exception.ValidationFailedException;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/admin/settings")
public class AdminSettingsController {

	private final AdminSettingsService adminSettingsService;
	private final CustomerService customerService;
	private final MemberService memberService;

	/**
	 * 관리자 페이지에 값들 조회하는 컨트롤러
	 */
	@Admin
	@GetMapping
	public ResponseEntity<ResponseAdminSettingsDTO> getAdminSettings() {
		ResponseAdminSettingsDTO response = adminSettingsService.getAdminSettings();

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	/**
	 * 관리자 회원 관리 페이지에서 회원 조회하는 컨트롤러
	 */
	@Admin
	@GetMapping("/members")
	public ResponseEntity<Page<ResponseAdminSettingsMembersDTO>> getAdminSettingsMembers(
		@PageableDefault(page = 0, size = 10) Pageable pageable) {
		Page<ResponseAdminSettingsMembersDTO> response = adminSettingsService.getAdminSettingsMembers(pageable);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	/**
	 * 관리자 회원 관리 페이지에서 회원 상태 바꾸는 컨트롤러
	 */
	@Admin
	@PostMapping("/members/{memberId}")
	public ResponseEntity<Void> updateAdminSettingsMemberState(@PathVariable("memberId") String memberId,
		@Validated @RequestBody RequestAdminSettingsMemberStateDTO requestAdminSettingsMemberStateDTO,
		BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			throw new ValidationFailedException(bindingResult);
		}
		memberService.updateMemberState(memberId, requestAdminSettingsMemberStateDTO);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 회원의 권한을 수정하는 메서드
	 */
	@Admin
	@PutMapping("/members/{memberId}")
	public ResponseEntity<Void> updateAdminSettingsMemberRole(@PathVariable("memberId") String memberId) {
		memberService.updateMemberRole(memberId);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 회원 상태를 탈퇴로 바꾸는 메서드
	 */
	@Admin
	@DeleteMapping("/members/{memberId}")
	public ResponseEntity<Void> deleteAdminSettingsMember(@PathVariable("memberId") String memberId) {
		memberService.withdrawMember(memberId);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@Admin
	@GetMapping("/customers")
	public ResponseEntity<Page<ResponseAdminSettingsNonMembersDTO>> getAdminSettingsNonMembers(
		@PageableDefault(page = 0, size = 10) Pageable pageable) {
		Page<ResponseAdminSettingsNonMembersDTO> response = customerService.getAdminSettingsNonMembers(pageable);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

}
