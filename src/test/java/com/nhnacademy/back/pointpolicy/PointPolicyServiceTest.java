package com.nhnacademy.back.pointpolicy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nhnacademy.back.pointpolicy.domain.dto.request.RequestPointPolicyRegisterDTO;
import com.nhnacademy.back.pointpolicy.domain.dto.request.RequestPointPolicyUpdateDTO;
import com.nhnacademy.back.pointpolicy.domain.entity.PointPolicy;
import com.nhnacademy.back.pointpolicy.domain.entity.PointPolicyType;
import com.nhnacademy.back.pointpolicy.exception.PointPolicyAlreadyExistsException;
import com.nhnacademy.back.pointpolicy.exception.PointPolicyNotFoundException;
import com.nhnacademy.back.pointpolicy.repository.PointPolicyJpaRepository;
import com.nhnacademy.back.pointpolicy.service.impl.PointPolicyServiceImpl;

@ExtendWith(MockitoExtension.class)
class PointPolicyServiceImplTest {

	@Mock
	PointPolicyJpaRepository repository;

	@InjectMocks
	PointPolicyServiceImpl service;

	@Test
	@DisplayName("포인트 정책 생성 성공")
	void createPointPolicy_success() {
		RequestPointPolicyRegisterDTO dto = new RequestPointPolicyRegisterDTO();
		dto.setPointPolicyName("Test Policy");
		dto.setPointPolicyType(PointPolicyType.REGISTER);
		dto.setPointPolicyFigure(10L);

		when(repository.existsByPointPolicyName("Test Policy")).thenReturn(false);
		when(repository.findByPointPolicyTypeOrderByPointPolicyIsActiveDescPointPolicyCreatedAtDesc(PointPolicyType.REGISTER))
			.thenReturn(List.of());
		when(repository.save(any(PointPolicy.class))).thenAnswer(invocation -> invocation.getArgument(0));

		assertDoesNotThrow(() -> service.createPointPolicy(dto));

		verify(repository).save(any(PointPolicy.class));
	}

	@Test
	@DisplayName("포인트 정책 생성 실패 - 중복 이름")
	void createPointPolicy_alreadyExists() {
		RequestPointPolicyRegisterDTO dto = new RequestPointPolicyRegisterDTO();
		dto.setPointPolicyName("Duplicate Policy");

		when(repository.existsByPointPolicyName("Duplicate Policy")).thenReturn(true);

		assertThrows(PointPolicyAlreadyExistsException.class, () -> service.createPointPolicy(dto));
	}

	@Test
	@DisplayName("회원가입 포인트 정책 조회")
	void getRegisterPointPolicies() {
		PointPolicy policy = new PointPolicy(PointPolicyType.REGISTER, "policy1", 5, LocalDateTime.now());
		when(repository.findByPointPolicyTypeOrderByPointPolicyIsActiveDescPointPolicyCreatedAtDesc(PointPolicyType.REGISTER))
			.thenReturn(List.of(policy));

		var result = service.getRegisterPointPolicies();

		assertEquals(1, result.size());
		assertEquals("policy1", result.get(0).getPointPolicyName());
	}

	@Test
	@DisplayName("포인트 정책 활성화 성공")
	void activatePointPolicy_success() {
		PointPolicy policy1 = spy(new PointPolicy(PointPolicyType.REVIEW, "policy1", 10, LocalDateTime.now()));
		PointPolicy policy2 = spy(new PointPolicy(PointPolicyType.REVIEW, "policy2", 20, LocalDateTime.now()));

		when(repository.findById(1L)).thenReturn(Optional.of(policy1));
		when(repository.findByPointPolicyTypeOrderByPointPolicyIsActiveDescPointPolicyCreatedAtDesc(PointPolicyType.REVIEW))
			.thenReturn(List.of(policy1, policy2));
		when(repository.save(any(PointPolicy.class))).thenAnswer(invocation -> invocation.getArgument(0));

		service.activatePointPolicy(1L);

		verify(policy1).updateIsActive(true);
		verify(policy2).updateIsActive(false);
		verify(repository).save(policy1);
	}

	@Test
	@DisplayName("포인트 정책 활성화 실패 - 정책 없음")
	void activatePointPolicy_notFound() {
		when(repository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(PointPolicyNotFoundException.class, () -> service.activatePointPolicy(99L));
	}

	@Test
	@DisplayName("포인트 정책 수정 성공")
	void updatePointPolicy_success() {
		PointPolicy policy = spy(new PointPolicy(PointPolicyType.BOOK, "policyBook", 30, LocalDateTime.now()));

		RequestPointPolicyUpdateDTO dto = new RequestPointPolicyUpdateDTO();
		dto.setPointPolicyFigure(50L);

		when(repository.findById(1L)).thenReturn(Optional.of(policy));
		when(repository.save(any(PointPolicy.class))).thenAnswer(invocation -> invocation.getArgument(0));

		service.updatePointPolicy(1L, dto);

		verify(policy).updateFigure(50);
		verify(repository).save(policy);
	}

	@Test
	@DisplayName("포인트 정책 수정 실패 - 정책 없음")
	void updatePointPolicy_notFound() {
		RequestPointPolicyUpdateDTO dto = new RequestPointPolicyUpdateDTO();
		dto.setPointPolicyFigure(100L);

		when(repository.findById(10L)).thenReturn(Optional.empty());

		assertThrows(PointPolicyNotFoundException.class, () -> service.updatePointPolicy(10L, dto));
	}
}
