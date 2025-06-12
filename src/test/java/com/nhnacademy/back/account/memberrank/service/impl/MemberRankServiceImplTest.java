package com.nhnacademy.back.account.memberrank.service.impl;

import static org.mockito.Mockito.*;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nhnacademy.back.account.memberrank.domain.entity.MemberRank;
import com.nhnacademy.back.account.memberrank.domain.entity.RankName;
import com.nhnacademy.back.account.memberrank.repository.MemberRankJpaRepository;

@ExtendWith(MockitoExtension.class)
class MemberRankServiceImplTest {

	@InjectMocks
	private MemberRankServiceImpl memberRankService;

	@Mock
	private MemberRankJpaRepository memberRankJpaRepository;

	@Test
	@DisplayName("회원 등급 조회 메서드 테스트")
	void getMemberRanksMethodTest() throws Exception {

		// Given
		List<MemberRank> memberRanks = List.of(
			new MemberRank(1, RankName.NORMAL, 1, 1)
		);

		// When
		when(memberRankJpaRepository.findAll()).thenReturn(memberRanks);

		// Then
		Assertions.assertThatCode(() -> {
			memberRankService.getMemberRanks();
		}).doesNotThrowAnyException();

	}

}