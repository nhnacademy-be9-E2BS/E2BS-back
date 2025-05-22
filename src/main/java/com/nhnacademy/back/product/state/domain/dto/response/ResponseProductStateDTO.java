package com.nhnacademy.back.product.state.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseProductStateDTO {
	private long productStateId;
	private String productStateName;
}
