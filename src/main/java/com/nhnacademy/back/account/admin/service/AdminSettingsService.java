package com.nhnacademy.back.account.admin.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.account.admin.domain.domain.DailySummary;
import com.nhnacademy.back.account.admin.domain.domain.MonthlySummary;
import com.nhnacademy.back.account.admin.domain.dto.response.ResponseAdminSettingsDTO;
import com.nhnacademy.back.account.admin.domain.dto.response.ResponseAdminSettingsDailySummaryDTO;
import com.nhnacademy.back.account.admin.domain.dto.response.ResponseAdminSettingsMembersDTO;
import com.nhnacademy.back.account.admin.domain.dto.response.ResponseAdminSettingsMonthlySummaryDTO;
import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.account.member.service.MemberService;
import com.nhnacademy.back.order.order.repository.OrderDetailJpaRepository;
import com.nhnacademy.back.order.order.repository.OrderJpaRepository;
import com.nhnacademy.back.order.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminSettingsService {

	private final MemberService memberService;
	private final OrderService orderService;

	private final MemberJpaRepository memberJpaRepository;
	private final OrderJpaRepository orderJpaRepository;
	private final OrderDetailJpaRepository orderDetailJpaRepository;

	/**
	 * 관리자 페이지 조회를 위한 값들 조회 메서드
	 */
	public ResponseAdminSettingsDTO getAdminSettings() {
		int totalTodayLoginMembersCnt = memberService.getTotalTodayLoginMembersCnt();
		int totalMembersCnt = memberService.getTotalMemberCnt();
		long totalOrdersCnt = orderService.getAllOrders();
		long totalSales = orderService.getTotalSales();
		long totalMonthlySales = orderService.getTotalMonthlySales();
		long totalDailySales = orderService.getTotalDailySales();

		return ResponseAdminSettingsDTO.builder()
			.totalTodayLoginMembersCnt(totalTodayLoginMembersCnt)
			.totalMembersCnt(totalMembersCnt)
			.totalOrdersCnt(totalOrdersCnt)
			.totalSales(totalSales)
			.totalMonthlySales(totalMonthlySales)
			.totalDailySales(totalDailySales)
			.build();
	}

	/**
	 * 일자별 요악 데이터를 위한 데이터 조회
	 */
	public ResponseAdminSettingsDailySummaryDTO getAdminSettingsDailySummaries() {
		List<DailySummary> dailySummaries = new ArrayList<>();
		LocalDate today = LocalDate.now();

		for (int i = 0; i < 7; i++) {
			LocalDate date = today.minusDays(i);
			LocalDateTime start = date.atStartOfDay();
			LocalDateTime end = date.plusDays(1).atStartOfDay().minusNanos(1);

			int orderCount = orderJpaRepository.countOrdersByLocalDateTime(start, end);
			Long sales = orderDetailJpaRepository.getTotalDailySales(start, end);
			int signupCount = memberJpaRepository.countSignupMembersByLocalDate(date);

			dailySummaries.add(new DailySummary(date, orderCount, sales, signupCount));
		}

		return new ResponseAdminSettingsDailySummaryDTO(dailySummaries);
	}

	/**
	 * 이번 달 데이터 조회
	 */
	public ResponseAdminSettingsMonthlySummaryDTO getAdminSettingsMonthlySummary() {
		LocalDate today = LocalDate.now();
		LocalDate firstDayOfMonth = today.withDayOfMonth(1);
		LocalDate firstDayOfNextMonth = firstDayOfMonth.plusMonths(1);

		LocalDateTime start = firstDayOfMonth.atStartOfDay();
		LocalDateTime end = firstDayOfNextMonth.atStartOfDay().minusNanos(1);

		int orderCount = orderJpaRepository.countOrdersByLocalDateTime(start, end);
		Long sales = orderDetailJpaRepository.getTotalDailySales(start, end);
		int signupCount = memberJpaRepository.countSignupMembersByMonthlyDate(firstDayOfMonth, firstDayOfNextMonth);

		return new ResponseAdminSettingsMonthlySummaryDTO(new MonthlySummary(orderCount, sales, signupCount));
	}

	public Page<ResponseAdminSettingsMembersDTO> getAdminSettingsMembers(Pageable pageable) {
		Page<Member> members = memberJpaRepository.findAll(pageable);

		return members.map(member -> ResponseAdminSettingsMembersDTO.builder()
			.memberId(member.getMemberId())
			.customerName(member.getCustomer().getCustomerName())
			.customerEmail(member.getCustomer().getCustomerEmail())
			.memberRankName(member.getMemberRank().getMemberRankName().name())
			.memberState(member.getMemberState())
			.memberRole(member.getMemberRole().getMemberRoleName().name())
			.build());
	}

}
