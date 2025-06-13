package com.nhnacademy.back.account.memberrank.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.account.memberrank.domain.dto.response.ResponseMemberRankDTO;
import com.nhnacademy.back.account.memberrank.domain.entity.MemberRank;
import com.nhnacademy.back.account.memberrank.repository.MemberRankJpaRepository;
import com.nhnacademy.back.account.memberrank.service.MemberRankService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberRankServiceImpl implements MemberRankService {

	private final MemberRankJpaRepository memberRankJpaRepository;

	public List<ResponseMemberRankDTO> getMemberRanks() {
		List<MemberRank> memberRanks = memberRankJpaRepository.findAll();

		return memberRanks.stream()
			.map(memberRank -> ResponseMemberRankDTO.builder()
				.rankName(memberRank.getMemberRankName())
				.memberRankTierBonusRate(memberRank.getMemberRankTierBonusRate())
				.memberRankRequireAmount(memberRank.getMemberRankRequireAmount())
				.build())
			.toList();
	}

}
