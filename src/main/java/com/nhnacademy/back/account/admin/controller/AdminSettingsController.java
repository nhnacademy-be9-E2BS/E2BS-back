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
import com.nhnacademy.back.account.admin.domain.dto.response.ResponseAdminSettingsDailySummaryDTO;
import com.nhnacademy.back.account.admin.domain.dto.response.ResponseAdminSettingsMembersDTO;
import com.nhnacademy.back.account.admin.domain.dto.response.ResponseAdminSettingsMonthlySummaryDTO;
import com.nhnacademy.back.account.admin.domain.dto.response.ResponseAdminSettingsNonMembersDTO;
import com.nhnacademy.back.account.admin.service.AdminSettingsService;
import com.nhnacademy.back.account.customer.service.CustomerService;
import com.nhnacademy.back.account.member.exception.NotFoundMemberException;
import com.nhnacademy.back.account.member.exception.UpdateMemberRoleFailedException;
import com.nhnacademy.back.account.member.exception.UpdateMemberStateFailedException;
import com.nhnacademy.back.account.member.service.MemberService;
import com.nhnacademy.back.common.annotation.Admin;
import com.nhnacademy.back.common.exception.ValidationFailedException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "관리자 페이지 API", description = "관리자 화면 및 기능 제공")
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
	@Operation(summary = "관리자 페이지에 필요한 값 조회", description = "관리자 페이지에 필요한 정보 조회 기능")
	@Admin
	@GetMapping
	public ResponseEntity<ResponseAdminSettingsDTO> getAdminSettings() {
		ResponseAdminSettingsDTO response = adminSettingsService.getAdminSettings();

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	/**
	 * 관리자 페이지 일자별 요약 조회
	 */
	@Operation(summary = "관리자 페이지 정보 일자별 요약 조회", description = "관리자 페이지 일자별 요약 조회 기능")
	@Admin
	@GetMapping("/daily")
	public ResponseEntity<ResponseAdminSettingsDailySummaryDTO> getAdminSettingsDailySummaries() {
		ResponseAdminSettingsDailySummaryDTO responseAdminSettingsDailySummaryDTO = adminSettingsService.getAdminSettingsDailySummaries();

		return ResponseEntity.status(HttpStatus.CREATED).body(responseAdminSettingsDailySummaryDTO);
	}

	/**
	 * 관리자 페이지 이번 달 데이터 조회
	 */
	@Operation(summary = "관리자 페이지 이번 달 정보 조회", description = "관리자 페이지 이번 달 정보 조회 기능")
	@Admin
	@GetMapping("/monthly")
	public ResponseEntity<ResponseAdminSettingsMonthlySummaryDTO> getAdminSettingsMonthlySummary() {
		ResponseAdminSettingsMonthlySummaryDTO responseAdminSettingsMonthlySummaryDTO = adminSettingsService.getAdminSettingsMonthlySummary();

		return ResponseEntity.status(HttpStatus.CREATED).body(responseAdminSettingsMonthlySummaryDTO);
	}

	/**
	 * 관리자 회원 관리 페이지에서 회원 목록 조회하는 컨트롤러
	 */
	@Operation(summary = "관리자 페이지 회원 목록 조회", description = "관리자 페이지 회원 목록 조회 기능")
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
	@Operation(summary = "관리자 페이지 회원 상태 변경", description = "관리자 페이지 회원 상태 변경 기능",
		responses = {
			@ApiResponse(responseCode = "200", description = "회원 상태 변경 성공"),
			@ApiResponse(responseCode = "400", description = "입력값 검증 실패", content = @Content(schema = @Schema(implementation = ValidationFailedException.class))),
			@ApiResponse(responseCode = "500", description = "회원 찾기 실패", content = @Content(schema = @Schema(implementation = NotFoundMemberException.class))),
			@ApiResponse(responseCode = "500", description = "회원 상태 수정 실패", content = @Content(schema = @Schema(implementation = UpdateMemberStateFailedException.class)))
		})
	@Admin
	@PostMapping("/members/{memberId}")
	public ResponseEntity<Void> updateAdminSettingsMemberState(@PathVariable("memberId") String memberId,
		@Validated @Parameter(description = "회원 상태 요청 DTO", required = true, schema = @Schema(implementation = RequestAdminSettingsMemberStateDTO.class))
		@RequestBody RequestAdminSettingsMemberStateDTO requestAdminSettingsMemberStateDTO,
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
	@Operation(summary = "관리자 페이지 회원 권한 수정", description = "관리자 페이지 회원 권한 수정 기능",
		responses = {
			@ApiResponse(responseCode = "200", description = "회원 권한 변경 성공"),
			@ApiResponse(responseCode = "500", description = "회원 찾기 실패", content = @Content(schema = @Schema(implementation = NotFoundMemberException.class))),
			@ApiResponse(responseCode = "500", description = "회원 권한 변경 실패", content = @Content(schema = @Schema(implementation = UpdateMemberRoleFailedException.class)))
		})
	@Admin
	@PutMapping("/members/{memberId}")
	public ResponseEntity<Void> updateAdminSettingsMemberRole(@PathVariable("memberId") String memberId) {
		memberService.updateMemberRole(memberId);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 회원 상태를 탈퇴로 바꾸는 메서드
	 */
	@Operation(summary = "관리자 페이지 회원 탈퇴", description = "관리자 페이지 회원 탈퇴 기능",
		responses = {
			@ApiResponse(responseCode = "200", description = "회원 탈퇴 성공"),
			@ApiResponse(responseCode = "500", description = "회원 찾기 실패", content = @Content(schema = @Schema(implementation = NotFoundMemberException.class))),
			@ApiResponse(responseCode = "500", description = "회원 탈퇴 실패", content = @Content(schema = @Schema(implementation = UpdateMemberStateFailedException.class)))
		})
	@Admin
	@DeleteMapping("/members/{memberId}")
	public ResponseEntity<Void> deleteAdminSettingsMember(@PathVariable("memberId") String memberId) {
		memberService.withdrawMember(memberId);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@Operation(summary = "관리자 페이지 비회원 목록 조회", description = "관리자 페이지 비회원 목록 조회 기능")
	@Admin
	@GetMapping("/customers")
	public ResponseEntity<Page<ResponseAdminSettingsNonMembersDTO>> getAdminSettingsNonMembers(
		@PageableDefault(page = 0, size = 10) Pageable pageable) {
		Page<ResponseAdminSettingsNonMembersDTO> response = customerService.getAdminSettingsNonMembers(pageable);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

}
