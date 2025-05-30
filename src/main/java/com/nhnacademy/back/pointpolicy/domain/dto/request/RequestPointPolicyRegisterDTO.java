package com.nhnacademy.back.pointpolicy.domain.dto.request;

import com.nhnacademy.back.pointpolicy.domain.entity.PointPolicyType;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestPointPolicyRegisterDTO {

	@NotNull
	private PointPolicyType pointPolicyType;

	@NotNull
	private String pointPolicyName;

	@NotNull
	private Long pointPolicyFigure;

}
