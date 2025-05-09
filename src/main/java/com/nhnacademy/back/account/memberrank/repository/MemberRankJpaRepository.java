package com.nhnacademy.back.account.memberrank.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.account.memberrank.domain.entity.MemberRank;

public interface MemberRankJpaRepository extends JpaRepository<MemberRank, Long> {

	MemberRank getMemberRankByMemberRankId(long memberRankId);

}
