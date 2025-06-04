package com.nhnacademy.back.product.category;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nhnacademy.back.product.category.domain.dto.ProductCategoryFlatDTO;
import com.nhnacademy.back.product.category.domain.dto.response.ResponseCategoryIdsDTO;
import com.nhnacademy.back.product.category.repository.ProductCategoryJpaRepository;
import com.nhnacademy.back.product.category.service.impl.ProductCategoryServiceImpl;

@ExtendWith(MockitoExtension.class)
class ProductCategoryServiceTest {
	@InjectMocks
	private ProductCategoryServiceImpl productCategoryService;
	@Mock
	private ProductCategoryJpaRepository productCategoryJpaRepository;

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
