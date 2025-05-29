package com.nhnacademy.back.pointpolicy.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestPointPolicyUpdateDTO {

	@NotNull
	private Long pointPolicyFigure;

}
