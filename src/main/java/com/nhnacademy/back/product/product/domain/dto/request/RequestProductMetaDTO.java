package com.nhnacademy.back.product.product.domain.dto.request;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RequestProductMetaDTO {
	//상품상태
	@NotNull
	private Long productStateId;
	//출판사
	@NotNull
	private Long publisherId;

	//제목, 목차, 설명
	@NotNull
	private String productTitle;
	@NotNull
	private String productContent;
	@NotNull
	private String productDescription;

	//출판일시
	@NotNull
	private LocalDate productPublishedAt;
	//isbn
	@NotNull
	private String productIsbn;
	//정가
	@NotNull
	private Long productRegularPrice;
	//판매가
	@NotNull
	private Long productSalePrice;
	//포장가능여부
	@NotNull
	private boolean productPackageable;
	//상품재고
	@NotNull
	private Integer productStock;

	// 태그
	private List<Long> tagIds;
	// 카테고리 Id
	@NotNull
	private List<Long> categoryIds;
	// 기여자 Id
	@NotNull
	private List<Long> contributorIds;

}
