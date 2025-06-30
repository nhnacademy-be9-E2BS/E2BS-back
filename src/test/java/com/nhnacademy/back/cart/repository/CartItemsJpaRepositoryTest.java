package com.nhnacademy.back.cart.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.account.customer.respoitory.CustomerJpaRepository;
import com.nhnacademy.back.cart.domain.entity.Cart;
import com.nhnacademy.back.cart.domain.entity.CartItems;
import com.nhnacademy.back.product.product.domain.entity.Product;
import com.nhnacademy.back.product.product.repository.ProductJpaRepository;
import com.nhnacademy.back.product.publisher.domain.entity.Publisher;
import com.nhnacademy.back.product.publisher.repository.PublisherJpaRepository;
import com.nhnacademy.back.product.state.domain.entity.ProductState;
import com.nhnacademy.back.product.state.domain.entity.ProductStateName;
import com.nhnacademy.back.product.state.repository.ProductStateJpaRepository;

@ActiveProfiles("test")
@DataJpaTest
class CartItemsJpaRepositoryTest {

	@Autowired
	private CustomerJpaRepository customerJpaRepository;

	@Autowired
	private CartItemsJpaRepository cartItemsJpaRepository;

	@Autowired
	private CartJpaRepository cartJpaRepository;

	@Autowired
	private ProductJpaRepository productJpaRepository;

	@Autowired
	private ProductStateJpaRepository productStateJpaRepository;

	@Autowired
	private PublisherJpaRepository publisherJpaRepository;

	@Autowired
	private TestEntityManager entityManager;


	@Test
	@DisplayName("장바구니 항목 전체 삭제 쿼리 테스트")
	void deleteCartItemsByCart() {
		// given
		Customer customer = customerJpaRepository.save(
			Customer.builder()
				.customerEmail("test@example.com")
				.customerName("홍길동")
				.customerPassword("password")
				.build()
		);
		Cart cart = cartJpaRepository.save(new Cart(customer));

		Publisher publisher = publisherJpaRepository.save(new Publisher("a"));
		ProductState productState = productStateJpaRepository.save(new ProductState(ProductStateName.SALE));

		Product product1 = Product.builder()
			.productState(productState)
			.publisher(publisher)
			.productTitle("title1")
			.productContent("content1")
			.productDescription("description")
			.productPublishedAt(LocalDate.now())
			.productIsbn("isbn1")
			.productRegularPrice(10000)
			.productSalePrice(8000)
			.productPackageable(false)
			.productStock(1)
			.productImage(new ArrayList<>())
			.build();

		productJpaRepository.save(product1);

		CartItems cartItem1 = CartItems.builder()
			.cart(cart)
			.product(product1)
			.cartItemsQuantity(2)
			.build();

		cartItemsJpaRepository.save(cartItem1);

		entityManager.flush(); // given 으로 주어진 상황은 insert 쿼리 강제 실행과
		entityManager.clear(); // 영속성 컨텍스트 초기화 하여 엔티티들을 준영속 상태로 만듬 (실제 상황과 유사)

		// when
		cartItemsJpaRepository.deleteCartItemsByCart(cart);

		// then
		List<CartItems> remaining = cartItemsJpaRepository.findAll();
		assertThat(remaining).isEmpty();
	}

}
