package com.nhnacademy.back.account.customer.respoitory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

	@Query("SELECT c FROM Customer c WHERE c NOT IN (SELECT m.customer FROM Member m)")
	Page<Customer> findCustomersNotMembers(Pageable pageable);

	boolean existsByCustomerEmailAndCustomerPassword(String customerEmail, String customerPassword);
}
