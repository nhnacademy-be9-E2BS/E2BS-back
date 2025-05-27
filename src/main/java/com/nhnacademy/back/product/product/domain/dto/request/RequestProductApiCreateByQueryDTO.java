package com.nhnacademy.back.product.product.domain.dto.request;

import java.time.LocalDate;
import java.util.List;

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
public class RequestProductApiCreateByQueryDTO {

	@NotBlank
	private String queryType;

	@NotBlank
	private String publisherName;

	@NotBlank
	private String productTitle;

	@NotBlank
	private String productIsbn;

	@NotBlank
	private String productImage;

	@NotBlank
	private String productDescription;

	@NotNull
	private long productRegularPrice;

	@NotNull
	private long productSalePrice;

	@NotBlank
	private String contributors;

	@NotNull
	private LocalDate productPublishedAt;

	/**
	 * 아래부터는 관리자가 직접 입력하는 필드
	 */
	@NotBlank
	private String productContent;

	@NotNull
	private boolean productPackageable;

	@NotNull
	private int productStock;

	/**
	 *
	 */

	List<Long> categoryIds;
	List<Long> tagIds;
}
