package com.nhnacademy.back.product.product.domain.entity;

import java.time.LocalDate;
import java.util.List;

import com.nhnacademy.back.product.image.domain.entity.ProductImage;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductCreateDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductUpdateDTO;
import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
import com.nhnacademy.back.product.state.domain.entity.ProductState;
import com.nhnacademy.back.product.state.domain.entity.ProductStateName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long productId;

	@OneToOne(optional = false)
	@JoinColumn(name = "product_state_id")
	private ProductState productState;

	@ManyToOne(optional = false)
	@JoinColumn(name = "publisher_id")
	private Publisher publisher;

	@Column(length = 30, nullable = false)
	private String productTitle;

	@Column(nullable = false)
	private String productContent;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String productDescription;

	@Column(nullable = false)
	private LocalDate productPublishedAt;

	@Column(length = 20, nullable = false)
	private String productIsbn;

	@Column(nullable = false)
	private long productRegularPrice;

	@Column(nullable = false)
	private long productSalePrice;

	@Column(nullable = false)
	private boolean productPackageable;

	@Column(nullable = false)
	private int productStock;

	@Column(nullable = false, columnDefinition = "bigint DEFAULT 0")
	private long productHits = 0;

	@Column(nullable = false, columnDefinition = "bigint DEFAULT 0")
	private long productSearches = 0;

	@OneToMany(mappedBy = "product")
	private List<ProductImage> productImage;


	public static Product createProductEntity(RequestProductCreateDTO request, Publisher publisher) {
		return Product.builder()
			.productState(new ProductState(ProductStateName.SALE))
			.publisher(publisher)
			.productTitle(request.getProductTitle())
			.productContent(request.getProductContent())
			.productDescription(request.getProductDescription())
			.productIsbn(request.getProductIsbn())
			.productRegularPrice(request.getProductRegularPrice())
			.productSalePrice(request.getProductSalePrice())
			.productPackageable(request.isProductPackageable())
			.productStock(request.getProductStock())
			.productPublishedAt(LocalDate.now())
			.productHits(0)
			.productSearches(0)
			.build();
	}

	//updateProdut를 위해 set대신 쓴 생성자
	public void updateProduct(RequestProductUpdateDTO request, Publisher publisher, ProductState productState) {
		this.productId = request.getProductId();
		this.productState = productState;
		this.publisher = publisher;
		this.productTitle = request.getProductTitle();
		this.productContent = request.getProductContent();
		this.productDescription = request.getProductDescription();
		this.productRegularPrice = request.getProductRegularPrice();
		this.productSalePrice = request.getProductSalePrice();
		this.productPackageable = request.isProductPackageable();
		this.productStock = request.getProductStock();
	}

	public void setProduct(int productStock) {
		this.productStock = productStock;
	}

	public void setProduct(long productSalePrice) {
		this.productSalePrice = productSalePrice;
	}





}
