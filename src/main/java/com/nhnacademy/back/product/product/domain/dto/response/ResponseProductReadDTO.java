package com.nhnacademy.back.product.product.domain.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.nhnacademy.back.product.category.domain.dto.response.ResponseCategoryDTO;
import com.nhnacademy.back.product.contributor.domain.dto.response.ResponseContributorDTO;
import com.nhnacademy.back.product.image.domain.dto.response.ResponseProductImageDTO;
import com.nhnacademy.back.product.publisher.domain.dto.response.ResponsePublisherDTO;
import com.nhnacademy.back.product.state.domain.dto.response.ResponseProductStateDTO;
import com.nhnacademy.back.product.tag.domain.dto.response.ResponseTagDTO;

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
	// 상품상태, 출판사
	private ResponseProductStateDTO productState;
	private ResponsePublisherDTO publisher;
	private String productTitle;
	private String productContent;
	private String productDescription;
	private LocalDate productPublishedAt;
	private String productIsbn;
	private long productRegularPrice;
	private long productSalePrice;
	private boolean productPackageable;
	private int productStock;

	// 이미지
	private List<ResponseProductImageDTO> productImagePaths;
	// 태그
	private List<ResponseTagDTO> tags;
	// 카테고리
	private List<ResponseCategoryDTO> categories;
	// 기여자
	private List<ResponseContributorDTO> contributors;

	// 리뷰
	private double avgRating;
	private int reviewCount = 0;

	// 좋아요
	private boolean liked;
	private long likeCount;

}
