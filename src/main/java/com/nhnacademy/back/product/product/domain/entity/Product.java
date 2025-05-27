package com.nhnacademy.back.product.product.domain.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.nhnacademy.back.product.image.domain.entity.ProductImage;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductApiCreateByQueryDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductApiCreateDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductDTO;
import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
import com.nhnacademy.back.product.state.domain.entity.ProductState;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

	// product_id
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long productId;

	@ManyToOne(optional = false)
	@JoinColumn(name = "product_state_id")
	private ProductState productState;

	@ManyToOne(optional = false)
	@JoinColumn(name = "publisher_id")
	private Publisher publisher;

	@Column(length = 100, nullable = false)
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
	private List<ProductImage> productImage = new ArrayList<>();

	public static Product createProductApiEntity(RequestProductApiCreateDTO request, Publisher publisher,
		ProductState state) {
		Product product = Product.builder()
			.productState(state)
			.publisher(publisher)
			.productTitle(request.getProductTitle())
			.productDescription(request.getProductDescription())
			.productContent(request.getProductContent())
			.productIsbn(request.getProductIsbn())
			.productRegularPrice(request.getProductRegularPrice())
			.productSalePrice(request.getProductSalePrice())
			.productPackageable(request.isProductPackageable())
			.productStock(request.getProductStock())
			.productPublishedAt(request.getProductPublishedAt())
			.productHits(0)
			.productSearches(0)
			.productImage(new ArrayList<>())
			.build();

		ProductImage image = new ProductImage(product, request.getProductImage());
		product.getProductImage().add(image);

		return product;
	}

	public static Product createProductApiByQueryEntity(RequestProductApiCreateByQueryDTO request, Publisher publisher,
		ProductState state) {
		Product product = Product.builder()
			.productState(state)
			.publisher(publisher)
			.productTitle(request.getProductTitle())
			.productDescription(request.getProductDescription())
			.productContent(request.getProductContent())
			.productIsbn(request.getProductIsbn())
			.productRegularPrice(request.getProductRegularPrice())
			.productSalePrice(request.getProductSalePrice())
			.productPackageable(request.isProductPackageable())
			.productStock(request.getProductStock())
			.productPublishedAt(request.getProductPublishedAt())
			.productHits(0)
			.productSearches(0)
			.productImage(new ArrayList<>())
			.build();

		ProductImage image = new ProductImage(product, request.getProductImage());
		product.getProductImage().add(image);

		return product;
	}

	public static Product createProductEntity(RequestProductDTO request, ProductState productState,
		Publisher publisher) {
		return Product.builder()
			.productState(productState)
			.publisher(publisher)
			.productTitle(request.getProductTitle())
			.productContent(request.getProductContent())
			.productDescription(request.getProductDescription())
			.productPublishedAt(request.getProductPublishedAt())
			.productIsbn(request.getProductIsbn())
			.productRegularPrice(request.getProductRegularPrice())
			.productSalePrice(request.getProductSalePrice())
			.productPackageable(request.isProductPackageable())
			.productStock(request.getProductStock())
			.productHits(0)
			.productSearches(0)
			.productImage(new ArrayList<>())
			.build();
	}

	//updateProdut를 위해 set대신 쓴 생성자
	public void updateProduct(RequestProductDTO request, ProductState productState, Publisher publisher) {
		this.productState = productState;
		this.publisher = publisher;
		this.productTitle = request.getProductTitle();
		this.productContent = request.getProductContent();
		this.productDescription = request.getProductDescription();
		this.productIsbn = request.getProductIsbn();
		this.productRegularPrice = request.getProductRegularPrice();
		this.productSalePrice = request.getProductSalePrice();
		this.productPackageable = request.isProductPackageable();
		this.productStock = request.getProductStock();
	}

	public void setProductSale(int productStock) {
		this.productStock = productStock;
	}

	public void setProduct(long productSalePrice) {
		this.productSalePrice = productSalePrice;
	}

}