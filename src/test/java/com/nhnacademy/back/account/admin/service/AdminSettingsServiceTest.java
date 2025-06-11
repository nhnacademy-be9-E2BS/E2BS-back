package com.nhnacademy.back.account.admin.service;

import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.back.account.admin.domain.dto.response.ResponseAdminSettingsMembersDTO;
import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.account.member.service.MemberService;
import com.nhnacademy.back.account.memberrank.domain.entity.MemberRank;
import com.nhnacademy.back.account.memberrank.domain.entity.RankName;
import com.nhnacademy.back.account.memberrole.domain.entity.MemberRole;
import com.nhnacademy.back.account.memberrole.domain.entity.MemberRoleName;
import com.nhnacademy.back.account.memberstate.domain.entity.MemberState;
import com.nhnacademy.back.account.memberstate.domain.entity.MemberStateName;
import com.nhnacademy.back.account.socialauth.domain.entity.SocialAuth;
import com.nhnacademy.back.account.socialauth.domain.entity.SocialAuthName;
import com.nhnacademy.back.order.order.repository.OrderDetailJpaRepository;
import com.nhnacademy.back.order.order.repository.OrderJpaRepository;
import com.nhnacademy.back.order.order.service.OrderService;

@ExtendWith(MockitoExtension.class)
class AdminSettingsServiceTest {

	@InjectMocks
	private AdminSettingsService adminSettingsService;

	@Mock
	private MemberService memberService;

	@Mock
	private OrderService orderService;

	@Mock
	private MemberJpaRepository memberJpaRepository;

	@Mock
	private OrderJpaRepository orderJpaRepository;

	@Mock
	private OrderDetailJpaRepository orderDetailJpaRepository;

	@Test
	@DisplayName("관리자 페이지 조회를 위한 값 조회 메서드 테스트")
	void getAdminSettingsTest() throws Exception {

		// Given

		// When
		when(memberService.getTotalTodayLoginMembersCnt()).thenReturn(1);
		when(memberService.getTotalMemberCnt()).thenReturn(1);
		when(orderService.getAllOrders()).thenReturn(1L);
		when(orderService.getTotalSales()).thenReturn(1L);
		when(orderService.getTotalDailySales()).thenReturn(1L);
		when(orderService.getTotalDailySales()).thenReturn(1L);

		// Then
		Assertions.assertThatCode(() -> {
			adminSettingsService.getAdminSettings();
		}).doesNotThrowAnyException();

	}

	@Test
	@DisplayName("관리자 페이지 일자별 요약 데이터를 위한 데이터 조회 메서드 테스트")
	void getAdminSettingsDailySummaries() throws Exception {

		// Given

		// When
		when(orderJpaRepository.countOrdersByLocalDateTime(any(), any())).thenReturn(1);
		when(orderDetailJpaRepository.getTotalDailySales(any(), any())).thenReturn(1L);
		when(memberJpaRepository.countSignupMembersByLocalDate(any())).thenReturn(1);

		// Then
		Assertions.assertThatCode(() -> {
			adminSettingsService.getAdminSettingsDailySummaries();
		}).doesNotThrowAnyException();
	}

	@Test
	@DisplayName("관리자 페이지 이번 달 데이터 조회 메서드 테스트")
	void getAdminSettingsMonthlySummary() throws Exception {

		// Given

		// When
		when(orderJpaRepository.countOrdersByLocalDateTime(any(), any())).thenReturn(1); // ✔
		when(orderDetailJpaRepository.getTotalDailySales(any(), any())).thenReturn(1000L); // ✔
		when(memberJpaRepository.countSignupMembersByMonthlyDate(any(), any())).thenReturn(5); // ✔

		// Then
		Assertions.assertThatCode(() -> {
			adminSettingsService.getAdminSettingsMonthlySummary();
		}).doesNotThrowAnyException();

	}

	@Test
	@DisplayName("관리자 페이지 회원 목록 조회 메서드 테스트")
	void getAdminSettingsMembersMethodTest() throws Exception {

		// Given
		Pageable pageable = PageRequest.of(0, 10);

		Customer customer = Customer.builder()
			.customerId(1L)
			.customerName("김도윤")
			.customerEmail("user@naver.com")
			.build();

		Member member = Member.builder()
			.customer(customer)
			.memberId("user")
			.memberBirth(LocalDate.now())
			.memberPhone("010-1234-5678")
			.memberCreatedAt(LocalDate.now())
			.memberRank(new MemberRank(1L, RankName.NORMAL, 1, 1))
			.memberState(new MemberState(1L, MemberStateName.ACTIVE))
			.memberRole(new MemberRole(1L, MemberRoleName.MEMBER))
			.socialAuth(new SocialAuth(1L, SocialAuthName.WEB))
			.build();

		Page<Member> page = new PageImpl<>(List.of(member), pageable, 1);

		// When
		when(memberJpaRepository.findAll(pageable)).thenReturn(page);

		Page<ResponseAdminSettingsMembersDTO> result = adminSettingsService.getAdminSettingsMembers(pageable);

		// Then
		Assertions.assertThat(result.getContent()).hasSize(1);
		ResponseAdminSettingsMembersDTO dto = result.getContent().get(0);
		Assertions.assertThat(dto.getMemberId()).isEqualTo("user");

	}

}