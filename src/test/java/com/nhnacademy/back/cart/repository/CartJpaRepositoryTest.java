package com.nhnacademy.back.cart.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.account.customer.respoitory.CustomerJpaRepository;
import com.nhnacademy.back.cart.domain.entity.Cart;

@ActiveProfiles("test")
@DataJpaTest
class CartJpaRepositoryTest {

	@Autowired
	private CustomerJpaRepository customerJpaRepository;

	@Autowired
	private CartJpaRepository cartJpaRepository;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	@DisplayName("고객 ID로 장바구니 조회 쿼리 테스트")
	void findByCustomer_CustomerId() {
		// given
		Customer customer = customerJpaRepository.save(
			Customer.builder()
				.customerEmail("test@example.com")
				.customerName("홍길동")
				.customerPassword("password")
				.build()
		);

		cartJpaRepository.save(new Cart(customer));

		entityManager.flush();
		entityManager.clear();

		// when
		Optional<Cart> result = cartJpaRepository.findByCustomer_CustomerId(customer.getCustomerId());

		// then
		assertThat(result).isPresent();
		assertEquals("test@example.com", result.get().getCustomer().getCustomerEmail());
		assertThat(result.get().getCartItems()).isEmpty();
	}

}
