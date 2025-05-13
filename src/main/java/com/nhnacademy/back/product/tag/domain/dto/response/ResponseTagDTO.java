package com.nhnacademy.back.product.tag.domain.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseTagDTO {
	@NotNull
	private long tagId;
	@NotNull
	private String tagName;
}
