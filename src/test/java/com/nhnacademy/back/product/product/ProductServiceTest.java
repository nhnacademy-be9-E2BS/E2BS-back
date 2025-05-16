package com.nhnacademy.back.product.product;

import com.nhnacademy.back.product.image.domain.entity.ProductImage;
import com.nhnacademy.back.product.image.repository.ProductImageJpaRepository;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductCreateDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductGetDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductSalePriceUpdateDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductStockUpdateDTO;
import com.nhnacademy.back.product.product.domain.dto.request.RequestProductUpdateDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductCouponDTO;
import com.nhnacademy.back.product.product.domain.dto.response.ResponseProductReadDTO;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.exception.ProductAlreadyExistsException;
import com.nhnacademy.back.product.product.kim.service.impl.ProductServiceImpl;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;
import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
import com.nhnacademy.back.product.publisher.repository.PublisherJpaRepository;
import com.nhnacademy.back.product.state.domain.entity.ProductState;
import com.nhnacademy.back.product.state.domain.entity.ProductStateName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceTest {

	@InjectMocks
	private ProductServiceImpl productService;

	@Mock
	private ProductJpaRepository productJpaRepository;

	@Mock
	private ProductImageJpaRepository productImageJpaRepository;

	@Mock
	private PublisherJpaRepository publisherJpaRepository;

	private Publisher publisher;
	private Product product;
	private ProductState productState;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		publisher = new Publisher(1L, "Test Publisher");
		productState = new ProductState(ProductStateName.SALE);
		product = new Product(
			productState,
			publisher,
			"Test Book",
			"Contents",
			"Description",
			LocalDate.now(),
			"1234567890",
			10000L,
			9000L,
			true,
			10,
			0L,
			0L
		);
	}

	@Test
	void createProduct_success() {
		RequestProductCreateDTO request = new RequestProductCreateDTO(
			1L, "Test Book", "Contents", "Description", "1234567890",
			10000L, 9000L, true, 10, Arrays.asList("image1.jpg")
		);

		when(publisherJpaRepository.findById(1L)).thenReturn(Optional.of(publisher));
		when(productJpaRepository.existsByProductIsbn("1234567890")).thenReturn(false);
		when(productJpaRepository.save(any(Product.class))).thenReturn(product);
		when(productImageJpaRepository.save(any(ProductImage.class))).thenReturn(new ProductImage());

		productService.createProduct(request);

		verify(publisherJpaRepository).findById(1L);
		verify(productJpaRepository).existsByProductIsbn("1234567890");
		verify(productJpaRepository).save(any(Product.class));
		verify(productImageJpaRepository).save(any(ProductImage.class));
	}

	@Test
	void createProduct_productAlreadyExists_throwsException() {
		RequestProductCreateDTO request = new RequestProductCreateDTO(
			1L, "Test Book", "Contents", "Description", "1234567890",
			10000L, 9000L, true, 10, Arrays.asList("image1.jpg")
		);

		when(publisherJpaRepository.findById(1L)).thenReturn(Optional.of(publisher));
		when(productJpaRepository.existsByProductIsbn("1234567890")).thenReturn(true);

		assertThrows(ProductAlreadyExistsException.class, () -> productService.createProduct(request));

		verify(publisherJpaRepository).findById(1L);
		verify(productJpaRepository).existsByProductIsbn("1234567890");
		verify(productJpaRepository, never()).save(any(Product.class));
		verify(productImageJpaRepository, never()).save(any(ProductImage.class));
	}

	@Test
	void getProduct_success() {
		RequestProductGetDTO request = new RequestProductGetDTO();
		request.setProductId(1L);

		when(productJpaRepository.findById(1L)).thenReturn(Optional.of(product));
		when(productImageJpaRepository.findByProduct(product)).thenReturn(Arrays.asList(new ProductImage(product, "image1.jpg")));

		ResponseProductReadDTO result = productService.getProduct(request);

		assertNotNull(result);
		assertEquals(1L, result.getProductId());
		assertEquals("Test Book", result.getProductTitle());
		assertEquals("image1.jpg", result.getProductImage());

		verify(productJpaRepository).findById(1L);
		verify(productImageJpaRepository).findByProduct(product);
	}

	@Test
	void getProduct_notFound_throwsException() {
		RequestProductGetDTO request = new RequestProductGetDTO();
		request.setProductId(1L);

		when(productJpaRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> productService.getProduct(request));

		verify(productJpaRepository).findById(1L);
		verify(productImageJpaRepository, never()).findByProduct(any());
	}

	@Test
	void getProducts_pageable_success() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<Product> productPage = new PageImpl<>(Arrays.asList(product), pageable, 1);

		when(productJpaRepository.findAll(pageable)).thenReturn(productPage);
		when(productImageJpaRepository.findByProduct(product)).thenReturn(Arrays.asList(new ProductImage(product, "image1.jpg")));

		Page<ResponseProductReadDTO> result = productService.getProducts(pageable);

		assertNotNull(result);
		assertEquals(1, result.getTotalElements());
		assertEquals("Test Book", result.getContent().get(0).getProductTitle());
		assertEquals("image1.jpg", result.getContent().get(0).getProductImage());

		verify(productJpaRepository).findAll(pageable);
		verify(productImageJpaRepository).findByProduct(product);
	}

	@Test
	void getProducts_pageable_emptyImages() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<Product> productPage = new PageImpl<>(Arrays.asList(product), pageable, 1);

		when(productJpaRepository.findAll(pageable)).thenReturn(productPage);
		when(productImageJpaRepository.findByProduct(product)).thenReturn(Collections.emptyList());

		Page<ResponseProductReadDTO> result = productService.getProducts(pageable);

		assertNotNull(result);
		assertEquals(1, result.getTotalElements());
		assertNull(result.getContent().get(0).getProductImage());

		verify(productJpaRepository).findAll(pageable);
		verify(productImageJpaRepository).findByProduct(product);
	}

	// @Test
	// void getProducts_byIds_success() {
	// 	List<Long> productIds = Arrays.asList(1L);
	//
	// 	when(productJpaRepository.findAllById(productIds)).thenReturn(Arrays.asList(product));
	// 	when(productImageJpaRepository.findByProduct(product)).thenReturn(Arrays.asList(new ProductImage(product, "image1.jpg")));
	//
	// 	List<ResponseProductReadDTO> result = productService.getProducts(productIds);
	//
	// 	assertNotNull(result);
	// 	assertEquals(1, result.size());
	// 	assertEquals("Test Book", result.get(0).getProductTitle());
	// 	assertEquals("image1.jpg", result.get(0).getProductImage());
	//
	// 	verify(productJpaRepository).findAllById(productIds);
	// 	verify(productImageJpaRepository).findByProduct(product);
	// }

	@Test
	void updateProduct_success() {
		RequestProductUpdateDTO request = new RequestProductUpdateDTO(
			1L, 1L, 1L, "Updated Book", "Updated Contents", "Updated Description",
			12000L, 11000L, false, 20
		);

		when(productJpaRepository.findById(1L)).thenReturn(Optional.of(product));
		when(publisherJpaRepository.findById(1L)).thenReturn(Optional.of(publisher));
		when(productJpaRepository.save(any(Product.class))).thenReturn(product);

		productService.updateProduct(request);

		verify(productJpaRepository).findById(1L);
		verify(publisherJpaRepository).findById(1L);
		verify(productJpaRepository).save(any(Product.class));
	}

	@Test
	void updateProduct_productNotFound_throwsException() {
		RequestProductUpdateDTO request = new RequestProductUpdateDTO(
			1L, 1L, 1L, "Updated Book", "Updated Contents", "Updated Description",
			12000L, 11000L, false, 20
		);

		when(productJpaRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(request));

		verify(productJpaRepository).findById(1L);
		verify(publisherJpaRepository, never()).findById(anyLong());
		verify(productJpaRepository, never()).save(any(Product.class));
	}

	@Test
	void updateProduct_publisherNotFound_throwsException() {
		RequestProductUpdateDTO request = new RequestProductUpdateDTO(
			1L, 1L, 1L, "Updated Book", "Updated Contents", "Updated Description",
			12000L, 11000L, false, 20
		);

		when(productJpaRepository.findById(1L)).thenReturn(Optional.of(product));
		when(publisherJpaRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(request));

		verify(productJpaRepository).findById(1L);
		verify(publisherJpaRepository).findById(1L);
		verify(productJpaRepository, never()).save(any(Product.class));
	}

	@Test
	void updateProductStock_success() {
		RequestProductStockUpdateDTO request = new RequestProductStockUpdateDTO(1L, 15);

		when(productJpaRepository.findById(1L)).thenReturn(Optional.of(product));
		when(productJpaRepository.save(any(Product.class))).thenReturn(product);

		productService.updateProductStock(request);

		verify(productJpaRepository).findById(1L);
		verify(productJpaRepository).save(any(Product.class));
	}

	@Test
	void updateProductStock_productNotFound_throwsException() {
		RequestProductStockUpdateDTO request = new RequestProductStockUpdateDTO(1L, 15);

		when(productJpaRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> productService.updateProductStock(request));

		verify(productJpaRepository).findById(1L);
		verify(productJpaRepository, never()).save(any(Product.class));
	}

	@Test
	void updateProductSalePrice_success() {
		RequestProductSalePriceUpdateDTO request = new RequestProductSalePriceUpdateDTO(1L, 9500);

		when(productJpaRepository.findById(1L)).thenReturn(Optional.of(product));
		when(productJpaRepository.save(any(Product.class))).thenReturn(product);

		productService.updateProductSalePrice(request);

		verify(productJpaRepository).findById(1L);
		verify(productJpaRepository).save(any(Product.class));
	}

	@Test
	void updateProductSalePrice_productNotFound_throwsException() {
		RequestProductSalePriceUpdateDTO request = new RequestProductSalePriceUpdateDTO(1L, 9500);

		when(productJpaRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> productService.updateProductSalePrice(request));

		verify(productJpaRepository).findById(1L);
		verify(productJpaRepository, never()).save(any(Product.class));
	}

	@Test
	void getProductsToCoupon_success() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<Product> productPage = new PageImpl<>(Arrays.asList(product), pageable, 1);

		when(productJpaRepository.findByProductStockGreaterThanAndProductState_ProductStateName(
			0, ProductStateName.SALE, pageable)).thenReturn(productPage);

		Page<ResponseProductCouponDTO> result = productService.getProductsToCoupon(pageable);

		assertNotNull(result);
		assertEquals(1, result.getTotalElements());
		assertEquals("Test Book", result.getContent().get(0).getProductTitle());
		assertEquals("Test Publisher", result.getContent().get(0).getPublisherName());
		assertNull(result.getContent().get(0).getContributorName());

		verify(productJpaRepository).findByProductStockGreaterThanAndProductState_ProductStateName(
			0, ProductStateName.SALE, pageable);
	}
}