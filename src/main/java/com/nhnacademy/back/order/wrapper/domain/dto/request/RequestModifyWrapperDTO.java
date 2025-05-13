package com.nhnacademy.back.order.wrapper.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class RequestModifyWrapperDTO {
	@NotNull
	private boolean wrapperSaleable;
}
