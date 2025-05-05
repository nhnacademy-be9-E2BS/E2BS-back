package com.nhnacademy.back.pointhistory.domain.entity;

import java.time.LocalDateTime;

import com.nhnacademy.back.member.domain.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

@Getter
@Entity
public class PointHistory {

	@Id
	private long pointHistoryId;

	@Column(nullable = false)
	private long pointAmount = 0L;

	@Column(nullable = false)
	private String pointReason;

	@Column(nullable = false)
	private LocalDateTime pointCreatedAt;

	@ManyToOne(optional = false)
	private Member member;

}
