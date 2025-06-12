package com.nhnacademy.back.account.pointhistory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.member.exception.NotFoundMemberException;
import com.nhnacademy.back.account.member.repository.MemberJpaRepository;
import com.nhnacademy.back.account.pointhistory.domain.dto.response.ResponseMemberPointDTO;
import com.nhnacademy.back.account.pointhistory.domain.dto.response.ResponsePointHistoryDTO;
import com.nhnacademy.back.account.pointhistory.domain.entity.PointHistory;
import com.nhnacademy.back.account.pointhistory.exception.InsufficientPointException;
import com.nhnacademy.back.account.pointhistory.repository.PointHistoryJpaRepository;
import com.nhnacademy.back.account.pointhistory.service.impl.PointHistoryServiceImpl;
import com.nhnacademy.back.pointpolicy.domain.entity.PointPolicy;
import com.nhnacademy.back.pointpolicy.domain.entity.PointPolicyType;
import com.nhnacademy.back.pointpolicy.exception.PointPolicyNotFoundException;
import com.nhnacademy.back.pointpolicy.repository.PointPolicyJpaRepository;

@ExtendWith(MockitoExtension.class)
class PointHistoryServiceTest {

	@Mock
	MemberJpaRepository memberJpaRepository;

	@Mock
	PointHistoryJpaRepository pointHistoryJpaRepository;

	@Mock
	PointPolicyJpaRepository pointPolicyJpaRepository;

	@InjectMocks
	PointHistoryServiceImpl service;

	@Test
	@DisplayName("회원 포인트 총합 조회 성공")
	void getMemberPoints_Success() {
		String memberId = "user";

		Member mockMember = Mockito.mock(Member.class);

		when(memberJpaRepository.getMemberByMemberId(memberId)).thenReturn(mockMember);

		PointHistory ph1 = new PointHistory(100L, "reason", LocalDateTime.now(), mockMember);
		PointHistory ph2 = new PointHistory(50L, "reason2", LocalDateTime.now(), mockMember);

		when(pointHistoryJpaRepository.getPointHistoriesByMember(mockMember))
			.thenReturn(List.of(ph1, ph2));

		ResponseMemberPointDTO result = service.getMemberPoints(memberId);

		assertEquals(memberId, result.getMemberId());
		assertEquals(150L, result.getPointAmount());
	}

	@Test
	@DisplayName("회원 포인트 총합 조회 - 회원 없음 예외")
	void getMemberPoints_MemberNotFound() {
		when(memberJpaRepository.getMemberByMemberId("invalid")).thenReturn(null);

		assertThrows(NotFoundMemberException.class,
			() -> service.getMemberPoints("invalid"));
	}

	@Test
	@DisplayName("포인트 내역 페이징 조회 성공")
	void getPointHistoryByMemberId_Success() {
		String memberId = "user";

		Member mockMember = Mockito.mock(Member.class);

		when(memberJpaRepository.getMemberByMemberId(memberId)).thenReturn(mockMember);

		PointHistory ph = new PointHistory(100L, "reason", LocalDateTime.now(), mockMember);

		Page<PointHistory> page = new PageImpl<>(Collections.singletonList(ph));

		when(pointHistoryJpaRepository.findByMemberOrderByPointCreatedAtDesc(eq(mockMember), any(Pageable.class)))
			.thenReturn(page);

		Page<ResponsePointHistoryDTO> result = service.getPointHistoryByMemberId(memberId, Pageable.unpaged());

		assertEquals(1, result.getTotalElements());
		ResponsePointHistoryDTO dto = result.getContent().get(0);
		assertEquals(100L, dto.getPointAmount());
		assertEquals("reason", dto.getPointReason());
	}

	@Test
	@DisplayName("회원가입 포인트 적립 성공")
	void earnRegisterPoint_Success() {
		String memberId = "user";

		Member mockMember = Mockito.mock(Member.class);

		when(memberJpaRepository.getMemberByMemberId(memberId)).thenReturn(mockMember);

		PointPolicy policy = new PointPolicy(
			PointPolicyType.REGISTER,
			"회원가입 포인트 정책",
			100L,
			LocalDateTime.now()
		);
		when(pointPolicyJpaRepository.findByPointPolicyTypeAndPointPolicyIsActive(PointPolicyType.REGISTER, true))
			.thenReturn(policy);

		service.earnRegisterPoint(memberId);

		verify(pointHistoryJpaRepository, times(1)).save(any(PointHistory.class));
	}

	@Test
	@DisplayName("이미지 리뷰 포인트 적립 성공")
	void earnImgReviewPoint_Success() {
		String memberId = "user";

		Member mockMember = Mockito.mock(Member.class);

		when(memberJpaRepository.getMemberByMemberId(memberId)).thenReturn(mockMember);

		PointPolicy policy = new PointPolicy(
			PointPolicyType.REVIEW_IMG,
			"이미지 리뷰 포인트 정책",
			100L,
			LocalDateTime.now()
		);
		when(pointPolicyJpaRepository.findByPointPolicyTypeAndPointPolicyIsActive(PointPolicyType.REVIEW_IMG, true))
			.thenReturn(policy);

		service.earnImgReviewPoint(memberId);

		verify(pointHistoryJpaRepository, times(1)).save(any(PointHistory.class));
	}

	@Test
	@DisplayName("일반 리뷰 포인트 적립 성공")
	void earnReviewPoint_Success() {
		String memberId = "user";

		Member mockMember = Mockito.mock(Member.class);

		when(memberJpaRepository.getMemberByMemberId(memberId)).thenReturn(mockMember);

		PointPolicy policy = new PointPolicy(
			PointPolicyType.REVIEW,
			"일반 리뷰 포인트 정책",
			100L,
			LocalDateTime.now()
		);
		when(pointPolicyJpaRepository.findByPointPolicyTypeAndPointPolicyIsActive(PointPolicyType.REVIEW, true))
			.thenReturn(policy);

		service.earnReviewPoint(memberId);

		verify(pointHistoryJpaRepository, times(1)).save(any(PointHistory.class));
	}

	@Test
	@DisplayName("기본 적립률 포인트 적립 성공")
	void earnOrderPoint_Success() {
		long customerId = 1L;

		Member mockMember = Mockito.mock(Member.class);

		when(memberJpaRepository.getMemberByCustomerId(customerId)).thenReturn(mockMember);

		service.earnOrderPoint(1L, 3000L);

		verify(pointHistoryJpaRepository, times(1)).save(any(PointHistory.class));
	}

	@Test
	@DisplayName("회원가입 포인트 적립 - 활성화된 정책 없을 때 예외")
	void earnRegisterPoint_NoActivePolicy() {
		String memberId = "user";

		Member mockMember = Mockito.mock(Member.class);

		when(memberJpaRepository.getMemberByMemberId(memberId)).thenReturn(mockMember);

		when(pointPolicyJpaRepository.findByPointPolicyTypeAndPointPolicyIsActive(PointPolicyType.REGISTER, true))
			.thenReturn(null);

		assertThrows(PointPolicyNotFoundException.class, () -> service.earnRegisterPoint(memberId));
	}

	@Test
	@DisplayName("이미지 리뷰 포인트 적립 - 활성화된 정책 없을 때 예외")
	void earnImgReviewPoint_NoActivePolicy() {
		String memberId = "user";

		Member mockMember = Mockito.mock(Member.class);

		when(memberJpaRepository.getMemberByMemberId(memberId)).thenReturn(mockMember);

		when(pointPolicyJpaRepository.findByPointPolicyTypeAndPointPolicyIsActive(PointPolicyType.REVIEW_IMG, true))
			.thenReturn(null);

		assertThrows(PointPolicyNotFoundException.class, () -> service.earnImgReviewPoint(memberId));
	}

	@Test
	@DisplayName("일반 리뷰 포인트 적립 - 활성화된 정책 없을 때 예외")
	void earnReviewPoint_NoActivePolicy() {
		String memberId = "user";

		Member mockMember = Mockito.mock(Member.class);

		when(memberJpaRepository.getMemberByMemberId(memberId)).thenReturn(mockMember);

		when(pointPolicyJpaRepository.findByPointPolicyTypeAndPointPolicyIsActive(PointPolicyType.REVIEW, true))
			.thenReturn(null);

		assertThrows(PointPolicyNotFoundException.class, () -> service.earnReviewPoint(memberId));
	}

	@Test
	@DisplayName("포인트 사용 성공")
	void payPoint_Success() {
		Long customerId = 1L;
		Long pointToPay = 50L;

		Member mockMember = Mockito.mock(Member.class);
		when(mockMember.getMemberId()).thenReturn("user");

		when(memberJpaRepository.getMemberByCustomerId(customerId)).thenReturn(mockMember);

		// service 내부 getMemberPoints() 호출 시 반환값 mocking
		PointHistoryServiceImpl spyService = Mockito.spy(service);
		ResponseMemberPointDTO points = new ResponseMemberPointDTO("user", 100L);
		doReturn(points).when(spyService).getMemberPoints("user");

		spyService.payPoint(customerId, pointToPay);

		verify(pointHistoryJpaRepository).save(any(PointHistory.class));
	}

	@Test
	@DisplayName("포인트 사용 - 잔액 부족 예외")
	void payPoint_InsufficientPoint() {
		Long customerId = 1L;
		Long pointToPay = 150L;

		Member mockMember = Mockito.mock(Member.class);
		when(mockMember.getMemberId()).thenReturn("user");

		when(memberJpaRepository.getMemberByCustomerId(customerId)).thenReturn(mockMember);

		PointHistoryServiceImpl spyService = Mockito.spy(service);
		ResponseMemberPointDTO points = new ResponseMemberPointDTO("user", 100L);
		doReturn(points).when(spyService).getMemberPoints("user");

		assertThrows(InsufficientPointException.class,
			() -> spyService.payPoint(customerId, pointToPay));
	}

	@Test
	@DisplayName("포인트 복구 성공")
	void recoverPoint_Success() {
		long customerId = 1L;

		Member mockMember = Mockito.mock(Member.class);

		when(memberJpaRepository.getMemberByCustomerId(customerId)).thenReturn(mockMember);

		service.recoverPoint(1L, 3000L);

		verify(pointHistoryJpaRepository, times(1)).save(any(PointHistory.class));
	}

	@Test
	@DisplayName("포인트 회수 성공")
	void retrievePoint_Success() {
		long customerId = 1L;

		Member mockMember = Mockito.mock(Member.class);

		when(memberJpaRepository.getMemberByCustomerId(customerId)).thenReturn(mockMember);

		service.retrievePoint(1L, 3000L);

		verify(pointHistoryJpaRepository, times(1)).save(any(PointHistory.class));
	}
}
