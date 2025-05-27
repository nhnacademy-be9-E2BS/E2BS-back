package com.nhnacademy.back.account.admin.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.account.admin.domain.dto.response.ResponseAdminSettingsDTO;
import com.nhnacademy.back.account.admin.domain.dto.response.ResponseAdminSettingsMembersDTO;
import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.account.member.service.MemberService;
import com.nhnacademy.back.order.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminSettingsService {

	private final MemberService memberService;
	private final OrderService orderService;

	private final MemberJpaRepository memberJpaRepository;

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
