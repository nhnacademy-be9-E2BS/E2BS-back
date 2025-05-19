package com.nhnacademy.back.product.product.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestProductApiCreateDTO {
	@NotBlank
	private String productTitle; //제목

	@NotBlank
	private String publisherName; //출판사

	@NotBlank
	private String positionName; //저자
	@NotNull
	private String productDescription; //설명

	@NotNull
	private String productIsbn; //ISBN13
	@NotNull
	private long productRegularPrice; //정가
	private long productSalePrice; //판매가
	private boolean productPackageable; //커버 이미지

	private int productStock; //재고 상태


}
