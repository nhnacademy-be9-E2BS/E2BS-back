package com.nhnacademy.back.account.memberstate.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.account.memberstate.domain.entity.MemberState;
import com.nhnacademy.back.account.memberstate.domain.entity.MemberStateName;

public interface MemberStateJpaRepository extends JpaRepository<MemberState, Long> {

	MemberState getMemberStateByMemberStateId(long memberStateId);

	MemberState findMemberStateByMemberStateName(MemberStateName memberStateName);

}
