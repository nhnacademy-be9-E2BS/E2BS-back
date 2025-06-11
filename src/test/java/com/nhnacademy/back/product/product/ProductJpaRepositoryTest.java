package com.nhnacademy.back.product.product;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import com.nhnacademy.back.product.category.domain.entity.Category;
import com.nhnacademy.back.product.category.domain.entity.ProductCategory;
import com.nhnacademy.back.product.category.repository.CategoryJpaRepository;
import com.nhnacademy.back.product.category.repository.ProductCategoryJpaRepository;
import com.nhnacademy.back.product.image.domain.entity.ProductImage;
import com.nhnacademy.back.product.image.repository.ProductImageJpaRepository;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;
import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
import com.nhnacademy.back.product.publisher.repository.PublisherJpaRepository;
import com.nhnacademy.back.product.state.domain.entity.ProductState;
import com.nhnacademy.back.product.state.domain.entity.ProductStateName;
import com.nhnacademy.back.product.state.repository.ProductStateJpaRepository;

@DataJpaTest
@ActiveProfiles("test")
class ProductJpaRepositoryTest {

	@Autowired
	private ProductJpaRepository productJpaRepository;

	@Autowired
	private PublisherJpaRepository publisherJpaRepository;

	@Autowired
	private ProductStateJpaRepository productStateJpaRepository;

	@Autowired
	private CategoryJpaRepository categoryJpaRepository;

	@Autowired
	private ProductCategoryJpaRepository productCategoryJpaRepository;

	@Autowired
	private ProductImageJpaRepository productImageJpaRepository;

	@Test
	@DisplayName("ISBN으로 도서 존재 여부 확인 - 성공")
	void existsByProductIsbnSuccess() {
		// given
		Publisher publisher = new Publisher("Test Publisher");
		publisher = publisherJpaRepository.save(publisher);

		ProductState productState = new ProductState(ProductStateName.SALE);
		productState = productStateJpaRepository.save(productState);

		Product product = Product.builder()
			.productIsbn("1234567890123")
			.productTitle("Test Book")
			.productContent("Content")
			.productDescription("Description")
			.productRegularPrice(10000L)
			.productSalePrice(9000L)
			.productPackageable(true)
			.productStock(50)
			.productPublishedAt(LocalDate.now())
			.publisher(publisher)
			.productState(productState)
			.build();
		productJpaRepository.save(product);

		// when & then
		assertTrue(productJpaRepository.existsByProductIsbn("1234567890123"), "ISBN으로 도서가 존재해야 합니다.");
		assertFalse(productJpaRepository.existsByProductIsbn("9876543210987"), "존재하지 않는 ISBN은 false를 반환해야 합니다.");
	}

	@Test
	@DisplayName("판매 상태 도서 목록 조회 - 성공")
	void findAllByProductState_ProductStateNameSuccess() {
		// given
		Publisher publisher = new Publisher("Test Publisher");
		publisher = publisherJpaRepository.save(publisher);

		ProductState productState = new ProductState(ProductStateName.SALE);
		productState = productStateJpaRepository.save(productState);

		Product product = Product.builder()
			.productIsbn("1234567890123")
			.productTitle("Test Book")
			.productContent("Content")
			.productDescription("Description")
			.productRegularPrice(10000L)
			.productSalePrice(9000L)
			.productPackageable(true)
			.productStock(50)
			.productPublishedAt(LocalDate.now())
			.publisher(publisher)
			.productState(productState)
			.build();
		productJpaRepository.save(product);

		Pageable pageable = PageRequest.of(0, 10);

		// when & then
		Page<Product> result = productJpaRepository.findAllByProductState_ProductStateName(ProductStateName.SALE,
			pageable);
		assertEquals(1, result.getTotalElements(), "판매 상태 도서가 1개여야 합니다.");
		assertEquals("Test Book", result.getContent().get(0).getProductTitle(), "도서 제목이 일치해야 합니다.");
	}

	@Test
	@DisplayName("카테고리 ID로 도서 목록 조회 - 성공")
	void findAllByCategoryIdSuccess() {
		// given
		Publisher publisher = publisherJpaRepository.save(new Publisher("Test Publisher"));
		ProductState productState = productStateJpaRepository.save(new ProductState(ProductStateName.SALE));

		Product product = Product.builder()
			.productIsbn("1112223334445")
			.productTitle("Category Book")
			.productContent("Content")
			.productDescription("Description")
			.productRegularPrice(12000L)
			.productSalePrice(10000L)
			.productPackageable(true)
			.productStock(30)
			.productPublishedAt(LocalDate.now())
			.publisher(publisher)
			.productState(productState)
			.build();
		product = productJpaRepository.save(product);

		Category category = new Category("IT", null);
		category = categoryJpaRepository.save(category);

		ProductCategory productCategory = new ProductCategory(product, category);
		productCategoryJpaRepository.save(productCategory);

		Pageable pageable = PageRequest.of(0, 10);

		// when
		Page<Product> result = productJpaRepository.findAllByCategoryId(category.getCategoryId(), pageable);

		// then
		assertEquals(1, result.getTotalElements());
		assertEquals("Category Book", result.getContent().get(0).getProductTitle());
	}

	@Test
	@DisplayName("ID로 도서 조회 (이미지 포함) - 성공")
	void findByIdWithImagesSuccess() {
		// given
		Publisher publisher = publisherJpaRepository.save(new Publisher("Test Publisher"));
		ProductState productState = productStateJpaRepository.save(new ProductState(ProductStateName.SALE));

		Product product = Product.builder()
			.productIsbn("9998887776665")
			.productTitle("Image Book")
			.productContent("Content")
			.productDescription("Description")
			.productRegularPrice(15000L)
			.productSalePrice(12000L)
			.productPackageable(true)
			.productStock(10)
			.productPublishedAt(LocalDate.now())
			.publisher(publisher)
			.productState(productState)
			.productImage(new ArrayList<>())
			.build();
		product = productJpaRepository.save(product);

		ProductImage image = new ProductImage(product, "http://example.com/image.jpg");
		productImageJpaRepository.save(image);
		product.getProductImage().add(image);

		// when
		Optional<Product> result = productJpaRepository.findByIdWithImages(product.getProductId());

		// then
		assertTrue(result.isPresent());
		assertNotNull(result.get().getProductImage());
		assertEquals("http://example.com/image.jpg", result.get().getProductImage().getFirst().getProductImagePath());
	}

	@Test
	@DisplayName("출판사 ID로 도서 목록 조회 - 성공")
	void findAllByPublisherIdSuccess() {
		// given
		Publisher publisher = publisherJpaRepository.save(new Publisher("Publisher X"));
		ProductState productState = productStateJpaRepository.save(new ProductState(ProductStateName.SALE));

		Product product1 = Product.builder()
			.productIsbn("1010101010101")
			.productTitle("Publisher Book 1")
			.productContent("Content")
			.productDescription("Description")
			.productRegularPrice(10000L)
			.productSalePrice(9000L)
			.productPackageable(true)
			.productStock(20)
			.productPublishedAt(LocalDate.now())
			.publisher(publisher)
			.productState(productState)
			.build();
		productJpaRepository.save(product1);

		Product product2 = Product.builder()
			.productIsbn("2020202020202")
			.productTitle("Publisher Book 2")
			.productContent("Content")
			.productDescription("Description")
			.productRegularPrice(11000L)
			.productSalePrice(10000L)
			.productPackageable(true)
			.productStock(15)
			.productPublishedAt(LocalDate.now())
			.publisher(publisher)
			.productState(productState)
			.build();
		productJpaRepository.save(product2);

		// when
		var results = productJpaRepository.findAllByPublisher_PublisherId(publisher.getPublisherId());

		// then
		assertEquals(2, results.size());
		assertTrue(results.stream().anyMatch(p -> p.getProductTitle().equals("Publisher Book 1")));
		assertTrue(results.stream().anyMatch(p -> p.getProductTitle().equals("Publisher Book 2")));
	}
}