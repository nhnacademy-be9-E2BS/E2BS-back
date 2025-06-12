package com.nhnacademy.back.product.product;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nhnacademy.back.product.category.domain.entity.Category;
import com.nhnacademy.back.product.category.domain.entity.ProductCategory;
import com.nhnacademy.back.product.category.repository.CategoryJpaRepository;
import com.nhnacademy.back.product.category.repository.ProductCategoryJpaRepository;
import com.nhnacademy.back.product.contributor.domain.dto.response.ResponseContributorDTO;
import com.nhnacademy.back.product.contributor.repository.ProductContributorJpaRepository;
import com.nhnacademy.back.product.image.domain.entity.ProductImage;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseMainPageProductDTO;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;
import com.nhnacademy.back.product.product.service.impl.MainPageProductServiceImpl;
import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
import com.nhnacademy.back.product.state.domain.entity.ProductState;
import com.nhnacademy.back.product.state.domain.entity.ProductStateName;

@ExtendWith(MockitoExtension.class)
class MainPageProductServiceTest {

	@Mock
	ProductJpaRepository productJpaRepository;
	@Mock
	CategoryJpaRepository categoryJpaRepository;
	@Mock
	ProductCategoryJpaRepository productCategoryJpaRepository;
	@Mock
	ProductContributorJpaRepository productContributorJpaRepository;

	@InjectMocks
	MainPageProductServiceImpl service;

	@Test
	@DisplayName("카테고리 없으면 빈 리스트 반환")
	void getProductsByCategory_whenCategoryNotFound_returnsEmpty() {
		// given
		given(categoryJpaRepository.findCategoryByCategoryName("X")).willReturn(null);

		// when
		List<ResponseMainPageProductDTO> result = service.getProductsByCategory("X");

		// then
		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("카테고리 존재하나 상품 없으면 빈 리스트")
	void getProductsByCategory_whenNoProductMappings_returnsEmpty() {
		// given
		Category cat = new Category("Cat", null);
		given(categoryJpaRepository.findCategoryByCategoryName("Cat")).willReturn(cat);
		given(productCategoryJpaRepository.findProductIdByCategory(cat)).willReturn(List.of());

		// when
		List<ResponseMainPageProductDTO> result = service.getProductsByCategory("Cat");

		// then
		assertThat(result).isEmpty();
	}

	@Test
	@DisplayName("카테고리 매핑된 상품이 존재하면 DTO로 매핑")
	void getProductsByCategory_whenProductsExist_mapsCorrectly() {
		// given
		Category cat = new Category("Cat", null);
		given(categoryJpaRepository.findCategoryByCategoryName("Cat")).willReturn(cat);
		ProductCategory pc1 = new ProductCategory(product(10L), cat);
		ProductCategory pc2 = new ProductCategory(product(20L), cat);
		given(productCategoryJpaRepository.findProductIdByCategory(cat)).willReturn(List.of(pc1, pc2));
		Product p1 = product(10L);
		Product p2 = product(20L);
		given(productJpaRepository.findByIdWithImages(10L)).willReturn(Optional.of(p1));
		given(productJpaRepository.findByIdWithImages(20L)).willReturn(Optional.of(p2));
		ResponseContributorDTO contrib = new ResponseContributorDTO("p1","Alice");
		given(productContributorJpaRepository.findContributorDTOsByProductId(10L))
			.willReturn(List.of(contrib));
		given(productContributorJpaRepository.findContributorDTOsByProductId(20L))
			.willReturn(List.of());

		// when
		List<ResponseMainPageProductDTO> result = service.getProductsByCategory("Cat");

		// then
		assertThat(result).hasSize(2);
		ResponseMainPageProductDTO dto1 = result.get(0);
		assertThat(dto1.getProductId()).isEqualTo(10L);
		assertThat(dto1.getContributorName()).isEqualTo("Alice");
		assertThat(dto1.getProductImage()).isEqualTo("/path10.jpg");

		ResponseMainPageProductDTO dto2 = result.get(1);
		assertThat(dto2.getProductId()).isEqualTo(20L);
		assertThat(dto2.getContributorName()).isEqualTo("미상");
	}

	@Test
	@DisplayName("getBestSellerProducts는 getProductsByCategory 호출")
	void getBestSellerProducts_delegates() {
		MainPageProductServiceImpl spy = spy(service);
		doReturn(List.of()).when(spy).getProductsByCategory("베스트셀러");

		List<ResponseMainPageProductDTO> result = spy.getBestSellerProducts();

		assertThat(result).isEmpty();
		then(spy).should().getProductsByCategory("베스트셀러");
	}

	private static Product createForTest(long productId,
		String title,
		String description,
		LocalDate publishedAt,
		int salePrice) {
		return Product.builder()
			.productId(productId)
			.productState(new ProductState(ProductStateName.SALE))
			.publisher(new Publisher("TEST"))
			.productTitle(title)
			.productDescription(description)
			.productContent("")
			.productIsbn("")
			.productRegularPrice(salePrice)
			.productSalePrice(salePrice)
			.productPackageable(false)
			.productStock(0)
			.productPublishedAt(publishedAt)
			.productImage(new ArrayList<>())
			.build();
	}


	private Product product(long id) {
		Product p = createForTest(
			id,
			"Title" + id,
			"Desc"  + id,
			LocalDate.of(2025, 1, 1),
			1000    // salePrice
		);

		p.getProductImage().add(new ProductImage(p, "/path" + id + ".jpg"));

		return p;
	}

}
