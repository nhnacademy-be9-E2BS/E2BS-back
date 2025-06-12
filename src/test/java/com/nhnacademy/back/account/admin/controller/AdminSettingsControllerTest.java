package com.nhnacademy.back.account.admin.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.back.account.admin.domain.domain.MonthlySummary;
import com.nhnacademy.back.account.admin.domain.dto.request.RequestAdminSettingsMemberStateDTO;
import com.nhnacademy.back.account.admin.domain.dto.response.ResponseAdminSettingsDTO;
import com.nhnacademy.back.account.admin.domain.dto.response.ResponseAdminSettingsDailySummaryDTO;
import com.nhnacademy.back.account.admin.domain.dto.response.ResponseAdminSettingsMembersDTO;
import com.nhnacademy.back.account.admin.domain.dto.response.ResponseAdminSettingsMonthlySummaryDTO;
import com.nhnacademy.back.account.admin.domain.dto.response.ResponseAdminSettingsNonMembersDTO;
import com.nhnacademy.back.account.admin.service.AdminSettingsService;
import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.account.customer.service.CustomerService;
import com.nhnacademy.back.account.member.service.MemberService;
import com.nhnacademy.back.account.memberstate.domain.entity.MemberState;
import com.nhnacademy.back.account.memberstate.domain.entity.MemberStateName;

@WebMvcTest(controllers = AdminSettingsController.class)
class AdminSettingsControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AdminSettingsService adminSettingsService;

	@MockitoBean
	private CustomerService customerService;

	@MockitoBean
	private MemberService memberService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("관리자 페이지 필요한 값 조회 테스트")
	void getAdminSettingsTest() throws Exception {

		// Given
		ResponseAdminSettingsDTO responseAdminSettingsDTO = ResponseAdminSettingsDTO.builder()
			.totalTodayLoginMembersCnt(100)
			.totalMembersCnt(100)
			.totalOrdersCnt(10000L)
			.totalSales(10000000L)
			.totalMonthlySales(1000000L)
			.totalDailySales(100000L)
			.build();

		// When
		when(adminSettingsService.getAdminSettings()).thenReturn(responseAdminSettingsDTO);

		// Then
		mockMvc.perform(get("/api/auth/admin/settings"))
			.andExpect(status().isOk());

	}

	@Test
	@DisplayName("관리자 페이지 정보 일자별 요약 조회 테스트")
	void getAdminSettingsDailyTest() throws Exception {

		// Given
		ResponseAdminSettingsDailySummaryDTO responseAdminSettingsDailySummaryDTO = new ResponseAdminSettingsDailySummaryDTO(
			List.of()
		);

		// When
		when(adminSettingsService.getAdminSettingsDailySummaries()).thenReturn(responseAdminSettingsDailySummaryDTO);

		// Then
		mockMvc.perform(get("/api/auth/admin/settings/daily"))
			.andExpect(status().isOk());

	}

	@Test
	@DisplayName("관리자 페이지 이번 달 정보 조회 테스트")
	void getAdminSettingsMonthlyTest() throws Exception {

		// Given
		ResponseAdminSettingsMonthlySummaryDTO responseAdminSettingsMonthlySummaryDTO = new ResponseAdminSettingsMonthlySummaryDTO(
			new MonthlySummary()
		);

		// When
		when(adminSettingsService.getAdminSettingsMonthlySummary()).thenReturn(responseAdminSettingsMonthlySummaryDTO);

		// Then
		mockMvc.perform(get("/api/auth/admin/settings/monthly"))
			.andExpect(status().isOk());

	}

	@Test
	@DisplayName("관리자 페이지 회원 목록 조회 테스트")
	void getAdminSettingsMembersTest() throws Exception {

		// Given
		ResponseAdminSettingsMembersDTO member = ResponseAdminSettingsMembersDTO.builder()
			.memberId("user001")
			.customerName("김도윤")
			.customerEmail("doyun@example.com")
			.memberRankName("일반회원")
			.memberState(new MemberState(1, MemberStateName.ACTIVE))
			.memberRole("ROLE_USER")
			.build();

		Page<ResponseAdminSettingsMembersDTO> page = new PageImpl<>(
			List.of(member),
			PageRequest.of(0, 10),
			1
		);

		// When
		when(adminSettingsService.getAdminSettingsMembers(PageRequest.of(0, 10)))
			.thenReturn(page);

		// Then
		mockMvc.perform(get("/api/auth/admin/settings/members")
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk());

	}

	@Test
	@DisplayName("관리자 페이지 회원 상태 변경 테스트")
	void updateAdminSettingsMemberStateTest() throws Exception {

		// Given
		RequestAdminSettingsMemberStateDTO requestAdminSettingsMemberStateDTO = new RequestAdminSettingsMemberStateDTO(
			"ACTIVE"
		);

		// When
		doNothing().when(memberService).updateMemberState("user", requestAdminSettingsMemberStateDTO);

		// Then
		mockMvc.perform(post("/api/auth/admin/settings/members/user")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestAdminSettingsMemberStateDTO)))
			.andExpect(status().isCreated());

	}

	@Test
	@DisplayName("관리자 페이지 회원 권한 수정 테스트")
	void updateAdminSettingsMembersRoleTest() throws Exception {

		// Given

		// When
		doNothing().when(memberService).updateMemberRole("user");

		// Then
		mockMvc.perform(put("/api/auth/admin/settings/members/user"))
			.andExpect(status().isOk());

	}

	@Test
	@DisplayName("관리자 페이지 회원 탈퇴 테스트")
	void deleteAdminSettingsMemberTest() throws Exception {

		// Given

		// When
		doNothing().when(memberService).withdrawMember("user");

		// Then
		mockMvc.perform(delete("/api/auth/admin/settings/members//user"))
			.andExpect(status().isOk());

	}

	@Test
	@DisplayName("관리자 페이지 비회원 목록 조회 테스트")
	void getAdminSettingsNonMembersTest() throws Exception {

		// Given
		ResponseAdminSettingsNonMembersDTO responseAdminSettingsNonMembersDTO = new ResponseAdminSettingsNonMembersDTO(
			new Customer("user@naver.com", "1234", "user")
		);

		Page<ResponseAdminSettingsNonMembersDTO> page = new PageImpl<>(
			List.of(responseAdminSettingsNonMembersDTO),
			PageRequest.of(0, 10),
			1
		);

		// When
		when(customerService.getAdminSettingsNonMembers(PageRequest.of(0, 10)))
			.thenReturn(page);

		// Then
		mockMvc.perform(get("/api/auth/admin/settings/customers")
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk());

	}

}