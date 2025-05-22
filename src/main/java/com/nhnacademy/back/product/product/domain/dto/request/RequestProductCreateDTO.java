package com.nhnacademy.back.product.product.domain.dto.request;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestProductCreateDTO {
	/**
	 * 도서를 생성할 때 필요한 정보
	 */

	//상품상태
	@NotNull
	private long productStateId;
	//출판사
	@NotNull
	private long publisherId;

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
	private long productRegularPrice;
	//판매가
	@NotNull
	private long productSalePrice;
	//포장가능여부
	@NotNull
	private boolean productPackageable;
	//상품재고
	@NotNull
	private int productStock;

	// 이미지
	@NotNull
	private List<String> productImagePaths;
	// 태그
	private List<Long> tagIds;
	// 카테고리 Id
	@NotNull
	private List<Long> categoryIds;
	// 기여자 Id
	@NotNull
	private List<Long> contributorIds;

}
