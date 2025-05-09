package com.nhnacademy.back.order.wrapper.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ResponseWrapperDTO {
	private long wrapperId;
	private long wrapperPrice;
	private String wrapperName;
	private String wrapperImage;
	private boolean wrapperSaleable;
}
