package com.nhnacademy.back.product.product.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestProductGetDTO {
	/**
	 * 도서 단건 조회를 위해 필요한 정보
	 */
	@NotNull
	String productIsbn;


}
