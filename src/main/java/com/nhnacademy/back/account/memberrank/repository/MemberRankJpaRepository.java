package com.nhnacademy.back.account.memberrank.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.account.memberstate.domain.entity.MemberState;

public interface MemberRankJpaRepository extends JpaRepository<MemberState,Long> {
}
