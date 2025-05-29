package com.nhnacademy.back.cart.domain.entity;

import java.util.ArrayList;
import java.util.List;

import com.nhnacademy.back.account.customer.domain.entity.Customer;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class 	Cart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long cartId;

	@OneToOne(optional = false)
	@JoinColumn(name = "customer_id")
	private Customer customer;

	/**
	 * CartRepository.delete(cart) 를 통해 카트를 통째로 삭제할 때 → cascade = CascadeType.ALL 이면 CartItems 도 같이 삭제됨
	 * CartItems 만 제거하려고 할 때 (ex) cart.getCartItems().remove(...)) → orphanRemoval = true 필수
	 *
	 * - cascade = CascadeType.ALL  : 부모 엔티티(Cart)에 수행한 작업(persist, remove 등)을 자식(CartItems)에도 전파
	 * - orphanRemoval = true	    : 부모 엔티티의 컬렉션(List 등)에서 자식 엔티티를 제거했을 때, 자식 엔티티를 DB 에서 삭제
	 */
	@OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<CartItems> cartItems = new ArrayList<>();

	public Cart(Customer customer) {
		this.customer = customer;
	}

}
