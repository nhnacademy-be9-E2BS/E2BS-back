package com.nhnacademy.back.pointpolicy.domain.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointPolicy {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long pointPolicyId;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private PointPolicyType pointPolicyType;

	@Column(length = 20, nullable = false)
	private String pointPolicyName;

	@Column(nullable = false)
	private long pointPolicyFigure;

	@Column(nullable = false)
	private LocalDateTime pointPolicyCreatedAt;

	@Column(nullable = false)
	private Boolean pointPolicyIsActive = true;

	public PointPolicy(PointPolicyType pointPolicyType, String pointPolicyName, long pointPolicyFigure, LocalDateTime pointPolicyCreatedAt) {
		this.pointPolicyType = pointPolicyType;
		this.pointPolicyName = pointPolicyName;
		this.pointPolicyFigure = pointPolicyFigure;
		this.pointPolicyCreatedAt = pointPolicyCreatedAt;
	}

	public void updateFigure(long pointPolicyFigure) {
		this.pointPolicyFigure = pointPolicyFigure;
	}

	public void updateIsActive(Boolean pointPolicyIsActive) {
		this.pointPolicyIsActive = pointPolicyIsActive;
	}
}
