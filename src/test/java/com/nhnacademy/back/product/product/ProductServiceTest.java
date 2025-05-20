package com.nhnacademy.back.product.product;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
import com.nhnacademy.back.product.product.exception.ProductNotFoundException;
import com.nhnacademy.back.product.product.exception.ProductStockDecrementException;
import com.nhnacademy.back.product.product.kim.service.impl.ProductServiceImpl;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;
import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
import com.nhnacademy.back.product.publisher.repository.PublisherJpaRepository;
import com.nhnacademy.back.product.state.domain.entity.ProductState;
import com.nhnacademy.back.product.state.domain.entity.ProductStateName;
import com.nhnacademy.back.product.state.repository.ProductStateJpaRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

	@InjectMocks
	private ProductServiceImpl productService;

	@Mock
	private ProductJpaRepository productJpaRepository;

	@Mock
	private ProductImageJpaRepository productImageJpaRepository;

	@Mock
	private PublisherJpaRepository publisherJpaRepository;

	@Mock
	private ProductStateJpaRepository productStateJpaRepository;

	@Test
	@DisplayName("도서 생성 - 성공")
	void createProductSuccess() {
		// given
		RequestProductCreateDTO request = new RequestProductCreateDTO(
			"Test Publisher", "Test Book", "Content", "Description", "1234567890123",
			10000L, 9000L, true, 50, List.of("image1.jpg"), List.of("tag1")
		);
		Publisher publisher = new Publisher("Test Publisher");
		when(publisherJpaRepository.findByPublisherName("Test Publisher")).thenReturn(publisher);
		when(productJpaRepository.existsByProductIsbn("1234567890123")).thenReturn(false);

		// when
		productService.createProduct(request);

		// then
		verify(productJpaRepository, times(1)).save(any(Product.class));
		verify(productImageJpaRepository, times(1)).save(any(ProductImage.class));
	}

	@Test
	@DisplayName("도서 생성 - 실패 (이미 존재)")
	void createProductFailAlreadyExists() {
		// given
		RequestProductCreateDTO request = new RequestProductCreateDTO(
			"Test Publisher", "Test Book", "Content", "Description", "1234567890123",
			10000L, 9000L, true, 50, List.of("image1.jpg"), List.of("tag1")
		);
		when(productJpaRepository.existsByProductIsbn("1234567890123")).thenReturn(true);

		// when & then
		assertThatThrownBy(() -> productService.createProduct(request))
			.isInstanceOf(ProductAlreadyExistsException.class);
	}

	@Test
	@DisplayName("도서 단건 조회 - 성공")
	void getProductSuccess() {
		// given
		RequestProductGetDTO request = new RequestProductGetDTO("1234567890123");
		Publisher publisher = new Publisher("Test Publisher");
		ProductState productState = new ProductState(ProductStateName.SALE);
		Product product = Product.builder()
			.productId(1L)
			.productTitle("Test Book")
			.productContent("Content")
			.productDescription("Description")
			.productIsbn("1234567890123")
			.productRegularPrice(10000L)
			.productSalePrice(9000L)
			.productPackageable(true)
			.productStock(50)
			.productPublishedAt(LocalDate.now())
			.publisher(publisher)
			.productState(productState)
			.build();
		when(productJpaRepository.findById(1L)).thenReturn(Optional.of(product));
		when(productImageJpaRepository.findByProduct_ProductId(1L)).thenReturn(List.of(new ProductImage(product, "image1.jpg")));

		// when
		ResponseProductReadDTO result = productService.getProduct(1L, request);

		// then
		assertThat(result.getProductTitle()).isEqualTo("Test Book");
		assertThat(result.getProductImagePaths()).containsExactly("image1.jpg");
	}

	@Test
	@DisplayName("도서 단건 조회 - 실패 (없음)")
	void getProductFailNotFound() {
		// given
		RequestProductGetDTO request = new RequestProductGetDTO("1234567890123");
		when(productJpaRepository.findById(1L)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> productService.getProduct(1L, request))
			.isInstanceOf(ProductNotFoundException.class);
	}

		@Test
		@DisplayName("도서 목록 조회 - 성공")
		void getProductsSuccess() {
			// given
			Pageable pageable = PageRequest.of(0, 10);
			Publisher publisher = new Publisher("Test Publisher");
			ProductState productState = new ProductState(ProductStateName.SALE);
			Product product = Product.builder()
				.productId(1L)
				.productTitle("Test Book")
				.productContent("Content")
				.productDescription("Description")
				.productIsbn("1234567890123")
				.productRegularPrice(10000L)
				.productSalePrice(9000L)
				.productPackageable(true)
				.productStock(50)
				.productPublishedAt(LocalDate.now())
				.publisher(publisher)
				.productState(productState)
				.build();
			Page<Product> page = new PageImpl<>(List.of(product), pageable, 1);
			when(productJpaRepository.findAll(pageable)).thenReturn(page);
			when(productImageJpaRepository.findByProduct_ProductId(1L)).thenReturn(List.of(new ProductImage(product, "image1.jpg")));

			// when
			Page<ResponseProductReadDTO> result = productService.getProducts(pageable);

			// then
			assertThat(result.getContent()).hasSize(1);
			assertThat(result.getContent().get(0).getProductTitle()).isEqualTo("Test Book");
		}

		@Test
		@DisplayName("도서 수정 - 성공")
		void updateProductSuccess() {
			// given
			RequestProductUpdateDTO request = new RequestProductUpdateDTO(
				"Sale", "Test Publisher", "Updated Book", "Updated Content", "Updated Description",
				12000L, 11000L, true, 60, List.of("image2.jpg"), List.of("tag1")
			);
			Publisher publisher = new Publisher("Test Publisher");
			ProductState productState = new ProductState(ProductStateName.SALE);
			Product product = Product.builder()
				.productId(1L)
				.productTitle("Test Book")
				.productContent("Content")
				.productDescription("Description")
				.productRegularPrice(10000L)
				.productSalePrice(9000L)
				.productPackageable(true)
				.productStock(50)
				.publisher(publisher)
				.productState(productState)
				.build();
			when(productJpaRepository.findById(1L)).thenReturn(Optional.of(product));
			when(publisherJpaRepository.findById(1L)).thenReturn(Optional.of(publisher));
			when(productStateJpaRepository.findById(1L)).thenReturn(Optional.of(productState));

			// when
			productService.updateProduct(1L, request);

			// then
			verify(productJpaRepository, times(1)).save(product);
			verify(productImageJpaRepository, times(1)).save(any(ProductImage.class));
			assertThat(product.getProductTitle()).isEqualTo("Updated Book");
		}

		@Test
		@DisplayName("도서 재고 수정 - 성공")
		void updateProductStockSuccess() {
			// given
			RequestProductStockUpdateDTO request = new RequestProductStockUpdateDTO(10);
			Product product = Product.builder()
				.productId(1L)
				.productStock(50)
				.build();
			when(productJpaRepository.findById(1L)).thenReturn(Optional.of(product));

			// when
			productService.updateProductStock(1L, request);

			// then
			verify(productJpaRepository, times(1)).save(product);
			assertThat(product.getProductStock()).isEqualTo(40);
		}

		@Test
		@DisplayName("도서 재고 수정 - 실패 (재고 부족)")
		void updateProductStockFailInsufficientStock() {
			// given
			RequestProductStockUpdateDTO request = new RequestProductStockUpdateDTO(60);
			Product product = Product.builder()
				.productId(1L)
				.productStock(50)
				.build();
			when(productJpaRepository.findById(1L)).thenReturn(Optional.of(product));

			// when & then
			assertThatThrownBy(() -> productService.updateProductStock(1L, request))
				.isInstanceOf(ProductStockDecrementException.class);
		}

	@Test
	@DisplayName("도서 판매가 수정 - 성공")
	void updateProductSalePriceSuccess() {
		// given
		long productId = 1L;
		long newSalePrice = 9500L;
		RequestProductSalePriceUpdateDTO request = new RequestProductSalePriceUpdateDTO(newSalePrice);
		Product product = Product.builder()
			.productId(productId)
			.productSalePrice(9000)
			.build();

		when(productJpaRepository.findById(productId)).thenReturn(Optional.of(product));

		// when
		productService.updateProductSalePrice(productId, request);

		// then
		verify(productJpaRepository, times(1)).findById(productId);
		verify(productJpaRepository, times(1)).save(product);
		assertThat(product.getProductSalePrice()).isEqualTo(newSalePrice);
	}

		@Test
		@DisplayName("쿠폰 적용 가능 도서 조회 - 성공")
		void getProductsForCouponSuccess() {
			// given
			Pageable pageable = PageRequest.of(0, 10);
			Publisher publisher = new Publisher("Test Publisher");
			ProductState productState = new ProductState(ProductStateName.SALE);
			Product product = Product.builder()
				.productId(1L)
				.productTitle("Test Book")
				.publisher(publisher)
				.productState(productState)
				.build();
			Page<Product> page = new PageImpl<>(List.of(product), pageable, 1);
			when(productJpaRepository.findAllByProductStateName(ProductStateName.SALE, pageable)).thenReturn(page);

			// when
			Page<ResponseProductCouponDTO> result = productService.getProductsToCoupon(pageable);

			// then
			assertThat(result.getContent()).hasSize(1);
			assertThat(result.getContent().get(0).getProductTitle()).isEqualTo("Test Book");
		}
	}