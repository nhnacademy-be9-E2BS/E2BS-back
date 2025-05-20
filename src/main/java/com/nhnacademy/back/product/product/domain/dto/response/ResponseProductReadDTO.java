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
	private boolean productPackageable;
	private int productStock;
	private List<String> productImagePaths;
}
