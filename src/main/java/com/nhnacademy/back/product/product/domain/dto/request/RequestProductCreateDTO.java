package com.nhnacademy.back.product.product.domain.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.nhnacademy.back.product.image.domain.entity.ProductImage;

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

	private long publisherId;
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
	private List<String> productImage;

}
