package com.nhnacademy.back.pointpolicy.domain.dto.response;

import java.time.LocalDateTime;

import com.nhnacademy.back.pointpolicy.domain.entity.PointPolicyType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResponsePointPolicyDTO {
	private Long pointPolicyId;
	private PointPolicyType pointPolicyType;
	private String pointPolicyName;
	private Long pointPolicyFigure;
	private LocalDateTime pointPolicyCreatedAt;
	private Boolean pointPolicyIsActive;
}
