package com.nhnacademy.back.product.product;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

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
	void findAllByProductStateNameSuccess() {
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
		Page<Product> result = productJpaRepository.findAllByProductStateName(ProductStateName.SALE, pageable);
		assertEquals(1, result.getTotalElements(), "판매 상태 도서가 1개여야 합니다.");
		assertEquals("Test Book", result.getContent().get(0).getProductTitle(), "도서 제목이 일치해야 합니다.");
	}
}