package com.nhnacademy.back.product.product.domain.dto.response;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseProductReadDTO {
	/**
	 * 도서의 상세 정보를 조회할 때 보내줘야 할 정보
	 */
	private long productId;
	private String productStateName;
	private String publisherName;
	private String productTitle;
	private String productContent;
	private String productDescription;
	private LocalDate productPublishedAt;
	private String productIsbn;
	private long productRegularPrice;
	private long productSalePrice;
	private boolean productPackageable;
	private int productStock;

	//이미지
	private List<String> productImagePaths;
	// 태그
	private List<String> tagNames;
	// 카테고리 Id
	private List<Long> categoryIds;
	// 기여자 Id
	private List<String> contributorNames;

}
