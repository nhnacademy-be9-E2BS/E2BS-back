package com.nhnacademy.back.pointpolicy.domain.entity;

import lombok.Getter;

@Getter
public enum PointPolicyType {
	REGISTER("회원가입 정책"),
	REVIEW_IMG("이미지 리뷰 정책"),
	REVIEW("일반 리뷰 정책"),
	BOOK("기본 적립률(%) 정책");

	private final String displayName;

	PointPolicyType(String displayName) {
		this.displayName = displayName;
	}

}