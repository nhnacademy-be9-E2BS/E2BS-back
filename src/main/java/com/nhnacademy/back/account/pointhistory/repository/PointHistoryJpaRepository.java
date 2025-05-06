package com.nhnacademy.back.account.pointhistory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.account.pointhistory.domain.entity.PointHistory;

public interface PointHistoryJpaRepository extends JpaRepository<PointHistory,Long> {
}
