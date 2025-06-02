package com.nhnacademy.back.account.memberrank.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.account.memberrank.domain.entity.MemberRank;
import com.nhnacademy.back.account.memberrank.domain.entity.RankName;

public interface MemberRankJpaRepository extends JpaRepository<MemberRank, Long> {

	MemberRank getMemberRankByMemberRankId(long memberRankId);

	MemberRank getMemberRankByMemberRankName(RankName memberRankName);
}
