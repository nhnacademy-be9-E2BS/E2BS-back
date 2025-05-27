package com.nhnacademy.back.product.category;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.nhnacademy.back.product.category.domain.dto.ProductCategoryFlatDTO;
import com.nhnacademy.back.product.category.domain.dto.response.ResponseCategoryIdsDTO;
import com.nhnacademy.back.product.category.domain.entity.Category;
import com.nhnacademy.back.product.category.domain.entity.ProductCategory;
import com.nhnacademy.back.product.category.exception.CategoryNotFoundException;
import com.nhnacademy.back.product.category.exception.ProductCategoryCreateNotAllowException;
import com.nhnacademy.back.product.category.repository.CategoryJpaRepository;
import com.nhnacademy.back.product.category.repository.ProductCategoryJpaRepository;
import com.nhnacademy.back.product.category.service.impl.ProductCategoryServiceImpl;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.exception.ProductNotFoundException;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;
import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
import com.nhnacademy.back.product.state.domain.entity.ProductState;
import com.nhnacademy.back.product.state.domain.entity.ProductStateName;

@ExtendWith(MockitoExtension.class)
class ProductCategoryServiceTest {
	@InjectMocks
	private ProductCategoryServiceImpl productCategoryService;
	@Mock
	private ProductCategoryJpaRepository productCategoryJpaRepository;
	@Mock
	private ProductJpaRepository productJpaRepository;
	@Mock
	private CategoryJpaRepository categoryJpaRepository;

	@Test
	@DisplayName("create productCategory - success")
	void create_productCategory_success_test() {
		// given
		long productId = 1L;
		List<Long> categoryIds = List.of(2L);
		boolean isUpdate = true;
		Product product = Product.builder()
			.productState(new ProductState(ProductStateName.SALE))
			.publisher(new Publisher("publisher"))
			.productTitle("Product")
			.productContent("Product content")
			.productDescription("Product description")
			.productPublishedAt(LocalDate.now())
			.productIsbn("978-89-12345-01-1")
			.productRegularPrice(10000)
			.productSalePrice(8000)
			.productPackageable(true)
			.productStock(100)
			.productHits(0)
			.productSearches(0)
			.productImage(new ArrayList<>())
			.build();
		Category categoryA = new Category("Category A", null);
		Category categoryB = new Category("Category B", categoryA);
		ReflectionTestUtils.setField(categoryA, "categoryId", 1L);
		ReflectionTestUtils.setField(categoryB, "categoryId", 2L);
		when(categoryJpaRepository.findById(1L)).thenReturn(Optional.of(categoryA));
		when(categoryJpaRepository.findById(2L)).thenReturn(Optional.of(categoryB));
		when(productJpaRepository.findById(productId)).thenReturn(Optional.of(product));

		// when
		productCategoryService.createProductCategory(productId, categoryIds, isUpdate);

		// then
		verify(productCategoryJpaRepository, times(2)).save(any(ProductCategory.class));
	}

	@Test
	@DisplayName("create productCategory - fail1")
	void create_productCategory_success_fail1_test() {
		// given
		long productId = 1L;
		List<Long> categoryIds = Collections.emptyList();
		boolean isUpdate = true;

		// when & then
		assertThatThrownBy(() -> productCategoryService.createProductCategory(productId, categoryIds, isUpdate))
			.isInstanceOf(ProductCategoryCreateNotAllowException.class);
	}

	@Test
	@DisplayName("create productCategory - fail2")
	void create_productCategory_success_fail2_test() {
		// given
		long productId = 1L;
		List<Long> categoryIds = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L);
		boolean isUpdate = true;

		// when & then
		assertThatThrownBy(() -> productCategoryService.createProductCategory(productId, categoryIds, isUpdate))
			.isInstanceOf(ProductCategoryCreateNotAllowException.class);
	}

	@Test
	@DisplayName("create productCategory - fail3")
	void create_productCategory_success_fail3_test() {
		// given
		long productId = 1L;
		List<Long> categoryIds = List.of(1L);
		boolean isUpdate = true;
		when(categoryJpaRepository.findById(1L)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> productCategoryService.createProductCategory(productId, categoryIds, isUpdate))
			.isInstanceOf(CategoryNotFoundException.class);
	}

	@Test
	@DisplayName("create productCategory - fail4")
	void create_productCategory_success_fail4_test() {
		long productId = 1L;
		List<Long> categoryIds = List.of(1L);
		boolean isUpdate = false;
		Category categoryA = new Category("Category A", null);
		ReflectionTestUtils.setField(categoryA, "categoryId", 1L);
		when(categoryJpaRepository.findById(1L)).thenReturn(Optional.of(categoryA));
		when(productJpaRepository.findById(anyLong())).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> productCategoryService.createProductCategory(productId, categoryIds, isUpdate))
			.isInstanceOf(ProductNotFoundException.class);
	}

	@Test
	@DisplayName("get categories by productId - success")
	void get_categories_by_productId_success_test() {
		// given
		List<Long> productIds = List.of(1L, 2L);
		List<ProductCategoryFlatDTO> mockResult = List.of(
			new ProductCategoryFlatDTO(1L, 10L),
			new ProductCategoryFlatDTO(1L, 11L),
			new ProductCategoryFlatDTO(2L, 20L)
		);
		when(productCategoryJpaRepository.findFlatCategoryData(productIds)).thenReturn(mockResult);

		// when
		List<ResponseCategoryIdsDTO> result = productCategoryService.getCategoriesByProductId(productIds);

		// then
		assertEquals(2, result.size());
		assertEquals(2, result.get(0).getCategoryIds().size());
		assertEquals(1, result.get(1).getCategoryIds().size());
	}
}
