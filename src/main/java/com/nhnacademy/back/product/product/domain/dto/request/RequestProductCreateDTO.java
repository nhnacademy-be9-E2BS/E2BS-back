package com.nhnacademy.back.product.product.domain.dto.request;

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

	//출판사
	private String publisherName;

	//제목, 목차, 설명
	@NotNull
	private String productTitle;
	@NotNull
	private String productContent;
	@NotNull
	private String productDescription;

	//isbn
	@NotNull
	private String productIsbn;
	//정가
	private long productRegularPrice;
	//판매가
	private long productSalePrice;
	//포장가능여부
	private boolean productPackageable;
	//재고
	private int productStock;
	// 이미지
	private List<String> productImagePaths;
	// 태그
	private List<String> tagNames;
	// 카테고리 Id
	private List<Long> categoryIds;
	// 기여자 Id
	private List<String> contributorNames;
	// 기여자 역할
	private List<String> positionNames; // 새로 추가


}
