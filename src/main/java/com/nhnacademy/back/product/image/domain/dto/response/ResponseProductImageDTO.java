package com.nhnacademy.back.product.image.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseProductImageDTO {
	private long productImageId;
	private String productImagePath;
}
