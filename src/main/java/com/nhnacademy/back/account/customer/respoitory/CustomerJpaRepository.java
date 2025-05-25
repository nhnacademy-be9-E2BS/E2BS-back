package com.nhnacademy.back.account.customer.respoitory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.nhnacademy.back.account.customer.domain.entity.Customer;

public interface CustomerJpaRepository extends JpaRepository<Customer, Long> {

	@Modifying(clearAutomatically = true)
	@Query("UPDATE Customer c SET c.customerName = :customerName, c.customerEmail = :customerEmail WHERE c.customerId = :customerId")
	int updateCustomerNameAndCustomerEmail(
		String customerName, String customerEmail, long customerId
	);

}
