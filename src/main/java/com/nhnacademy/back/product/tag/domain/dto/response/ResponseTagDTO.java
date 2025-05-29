package com.nhnacademy.back.product.tag.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseTagDTO {
	private Long tagId;
	private String tagName;
}
