package com.nhnacademy.back.elasticsearch.domain.document;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import com.nhnacademy.back.elasticsearch.domain.dto.request.RequestProductDocumentDTO;
import com.nhnacademy.back.product.product.domain.entity.Product;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Document(indexName = "e2bs_product")
@Setting(settingPath = "elasticsearch/product-setting.json")
@Mapping(mappingPath = "elasticsearch/product-mapping.json")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductDocument {

	// 도서 ID
	@Id
	@Field(type = FieldType.Long)
	private Long productId;

	// 도서명
	@Field(type = FieldType.Text)
	private String productTitle;

	// 도서 설명
	@Field(type = FieldType.Text)
	private String productContent;

	// 도서 출판일자
	@Field(type = FieldType.Date, format = DateFormat.basic_date)
	private LocalDate productPublishedAt;

	// 도서 판매가
	@Field(type = FieldType.Long)
	private Long productSalePrice;

	// 조회수 (상세페이지)
	@Field(type = FieldType.Long)
	private Long productHits;

	// 검색수
	@Field(type = FieldType.Long)
	private Long productSearches;

	// 평균 평점
	@Field(type = FieldType.Float)
	private Float productReviewRate;

	// 리뷰 수
	@Field(type = FieldType.Long)
	private Long productReviewCount;

	// 도서 저자 리스트
	@Field(type = FieldType.Keyword)
	private List<String> productContributors;

	// 도서 태그 리스트
	@Field(type = FieldType.Keyword)
	private List<String> productTags;

	// 도서 카테고리 ID 리스트
	@Field(type = FieldType.Long)
	private List<Long> productCategoryIds;

	public ProductDocument(RequestProductDocumentDTO request) {
		this.productId = request.getProductId();
		this.productTitle = request.getProductTitle();
		this.productContent = request.getProductContent();
		this.productPublishedAt = request.getProductPublishedAt();
		this.productSalePrice = request.getProductSalePrice();
		this.productReviewRate = 0f;
		this.productReviewCount = 0L;
		this.productContributors = request.getProductContributors();
		this.productTags = request.getProductTags();
		this.productCategoryIds = request.getProductCategoryIds();
		this.productHits = 0L;
		this.productSearches = 0L;
	}

	public ProductDocument(Product product, List<String> tags, List<String> contributors, List<Long> categoryIds) {
		this.productId = product.getProductId();
		this.productTitle = product.getProductTitle();
		this.productContent = product.getProductContent();
		this.productPublishedAt = product.getProductPublishedAt();
		this.productSalePrice = product.getProductSalePrice();
		this.productHits = product.getProductHits();
		this.productSearches = product.getProductSearches();
		this.productReviewRate = 0f;
		this.productReviewCount = 0L;
		this.productTags = tags;
		this.productContributors = contributors;
		this.productCategoryIds = categoryIds;
	}

	public void updateProductDocument(RequestProductDocumentDTO request) {
		this.productTitle = request.getProductTitle();
		this.productContent = request.getProductContent();
		this.productPublishedAt = request.getProductPublishedAt();
		this.productSalePrice = request.getProductSalePrice();
		this.productContributors = request.getProductContributors();
		this.productTags = request.getProductTags();
		this.productCategoryIds = request.getProductCategoryIds();
	}

	public void updateReview(Float reviewRate) {
		this.productReviewRate = reviewRate;
		this.productReviewCount++;
	}

	public void updateHits() {
		this.productHits++;
	}

	public void updateSearches() {
		this.productSearches++;
	}
}
