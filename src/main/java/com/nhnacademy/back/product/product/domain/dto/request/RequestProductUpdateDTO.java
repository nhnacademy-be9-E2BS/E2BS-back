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
public class RequestProductUpdateDTO {
	/**
	 * 도서를 수정할 때 필요한 정보
	 */
	private long productStateId;
	private long publisherId;
	@NotNull
	private String productTitle;
	@NotNull
	private String productContent;
	@NotNull
	private String productDescription;
	private long productRegularPrice;
	private long productSalePrice;
	private boolean productPackageable;
	private int productStock;
	private List<String> productImagePaths;

}
