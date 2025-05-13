package com.nhnacademy.back.cart.repository;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
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
@EntityScan(basePackages = {
	"com.nhnacademy.back.product.product.domain.entity",
	"com.nhnacademy.back.product.state.domain.entity",
	"com.nhnacademy.back.product.publisher.domain.entity",
	"com.nhnacademy.back.cart.domain.entity",
	"com.nhnacademy.back.account.customer.domain.entity"
})
public class CartItemsJpaRepositoryTest {

	@Autowired
	private CartItemsJpaRepository cartItemsRepository;

	@Autowired
	private CartJpaRepository cartRepository;

	@Autowired
	private CustomerJpaRepository customerRepository;

	@Autowired
	private ProductJpaRepository productRepository;

	@Autowired
	private ProductStateJpaRepository productStateRepository;

	@Autowired
	private PublisherJpaRepository publisherRepository;

	Customer customer;
	Cart cart;
	ProductState productState;
	Publisher publisher;
	Product product;

	@BeforeEach
	public void beforeEach() {
		customer = new Customer("abc@gmail.com", "pwd123", "name1");
		customerRepository.save(customer);

		cart = new Cart(customer);
		cartRepository.save(cart);

		productState = new ProductState(ProductStateName.SALE);
		publisher = new Publisher("publisher1");
		productStateRepository.save(productState);
		publisherRepository.save(publisher);

		product = new Product(productState, publisher,
			"title1", "content1", "description", LocalDate.now(), "isbn",
			10000, 8000, false, 1, 0, 0);
		productRepository.save(product);
	}


	@Test
	@DisplayName("Cart 와 Product 존재 여부 확인 테스트")
	void existsByCartAndProduct() {
		// given
		CartItems cartItem = new CartItems(cart, product, 2);
		cartItemsRepository.save(cartItem);

		// when
		boolean exists = cartItemsRepository.existsByCartAndProduct(cart, product);

		// then
		assertThat(exists).isTrue();
	}

	// @Test
	// void findByCart_Customer_CustomerId_페이지_조회() {
	// 	// given
	// 	Customer customer = em.persist(new Customer("user2", "user2@email.com"));
	// 	Cart cart = em.persist(new Cart(customer));
	//
	// 	for (int i = 0; i < 7; i++) {
	// 		Product product = em.persist(new Product("상품" + i, 1000L * i));
	// 		em.persist(new CartItems(cart, product, i + 1));
	// 	}
	//
	// 	Pageable pageable = PageRequest.of(0, 5);
	//
	// 	// when
	// 	Page<CartItems> page = cartItemsRepository.findByCart_Customer_CustomerId(customer.getCustomerId(), pageable);
	//
	// 	// then
	// 	assertThat(page.getTotalElements()).isEqualTo(7);
	// 	assertThat(page.getContent()).hasSize(5); // 한 페이지에 5개
	// 	assertThat(page.getTotalPages()).isEqualTo(2);
	// }

}
