package com.nhnacademy.back.account.pointhistory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.account.member.domain.entity.Member;
import com.nhnacademy.back.account.pointhistory.domain.entity.PointHistory;

public interface PointHistoryJpaRepository extends JpaRepository<PointHistory, Long> {

	List<PointHistory> getPointHistoriesByMember(Member member);

}
