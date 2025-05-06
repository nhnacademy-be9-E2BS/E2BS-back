package com.nhnacademy.back.account.pointhistory.domain.entity;

import java.time.LocalDateTime;

import com.nhnacademy.back.account.member.domain.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long pointHistoryId;

	@Column(nullable = false)
	private long pointAmount = 0L;

	@Column(nullable = false)
	private String pointReason;

	@Column(nullable = false)
	private LocalDateTime pointCreatedAt;

	@ManyToOne(optional = false)
	@JoinColumn(name = "customer_id")
	private Member member;

}
