package com.nhnacademy.back.product.like.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.account.customer.respoitory.CustomerJpaRepository;
import com.nhnacademy.back.product.like.domain.entity.Like;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;
import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
import com.nhnacademy.back.product.publisher.repository.PublisherJpaRepository;
import com.nhnacademy.back.product.state.domain.entity.ProductState;
import com.nhnacademy.back.product.state.domain.entity.ProductStateName;
import com.nhnacademy.back.product.state.repository.ProductStateJpaRepository;

@ActiveProfiles("test")
@DataJpaTest
class LikeJpaRepositoryTest {

	@Autowired
	private CustomerJpaRepository customerJpaRepository;

	@Autowired
	private ProductJpaRepository productJpaRepository;

	@Autowired
	private PublisherJpaRepository publisherJpaRepository;

	@Autowired
	private ProductStateJpaRepository productStateJpaRepository;

	@Autowired
	private LikeJpaRepository likeJpaRepository;

	@Autowired
	private TestEntityManager entityManager;


	Product product;

	@BeforeEach
	void setUp() {
		Publisher publisher = publisherJpaRepository.save(new Publisher("테스트출판사"));
		ProductState state = productStateJpaRepository.save(new ProductState(ProductStateName.SALE));

		product = productJpaRepository.save(Product.builder()
			.publisher(publisher)
			.productState(state)
			.productTitle("테스트책")
			.productContent("내용")
			.productDescription("설명")
			.productIsbn("isbn-001")
			.productPublishedAt(LocalDate.now())
			.productRegularPrice(15000)
			.productSalePrice(12000)
			.productPackageable(true)
			.productStock(100)
			.productImage(new ArrayList<>())
			.build());
	}


	@Test
	@DisplayName("회원이 해당 상품을 이미 좋아요 했는지 검증 테스트")
	void existsByProductAndCustomer() {
		// given
		Customer customer = customerJpaRepository.save(new Customer("email@a.com", "1234", "홍길동"));

		Like like = Like.createLikeEntity(product, customer);
		likeJpaRepository.save(like);

		entityManager.flush();
		entityManager.clear();

		// when
		boolean exists = likeJpaRepository.existsByProduct_ProductIdAndCustomer_CustomerId(product.getProductId(), customer.getCustomerId());

		// then
		assertTrue(exists);
	}

	@Test
	@DisplayName("회원 ID와 상품 ID에 해당하는 좋아요 단일 조회 테스트")
	void findByCustomerAndProduct() {
		// given
		Customer customer = customerJpaRepository.save(new Customer("email2@a.com", "pwd", "이순신"));
		Like like = likeJpaRepository.save(Like.createLikeEntity(product, customer));

		entityManager.flush();
		entityManager.clear();

		// when
		Optional<Like> result = likeJpaRepository.findByCustomer_CustomerIdAndProduct_ProductId(customer.getCustomerId(), product.getProductId());

		// then
		assertThat(result).isPresent();
		assertThat(result.get().getLikeId()).isEqualTo(like.getLikeId());
	}

	@Test
	@DisplayName("상품 ID로 좋아요 전체 개수 조회 테스트")
	void countLikesByProduct() {
		// given
		Customer customer1 = customerJpaRepository.save(new Customer("u1@a.com", "123", "유저1"));
		Customer customer2 = customerJpaRepository.save(new Customer("u2@a.com", "123", "유저2"));

		likeJpaRepository.save(Like.createLikeEntity(product, customer1));
		likeJpaRepository.save(Like.createLikeEntity(product, customer2));

		entityManager.flush();
		entityManager.clear();

		// when
		long count = likeJpaRepository.countAllByProduct_ProductId(product.getProductId());

		// then
		assertThat(count).isEqualTo(2);
	}

	@Test
	@DisplayName("회원이 좋아요한 상품 페이징 목록 조회 테스트")
	void findLikedProductsByCustomer() {
		// given
		Customer customer = customerJpaRepository.save(new Customer("like@a.com", "123", "김좋아요"));

		likeJpaRepository.save(Like.createLikeEntity(product, customer));

		entityManager.flush();
		entityManager.clear();

		Pageable pageable = PageRequest.of(0, 10);

		// when
		Page<Product> page = likeJpaRepository.findLikedProductsByCustomerId(customer.getCustomerId(), pageable);

		// then
		assertEquals(1, page.getTotalElements());
	}

}
