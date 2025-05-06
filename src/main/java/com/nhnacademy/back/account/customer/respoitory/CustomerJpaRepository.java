package com.nhnacademy.back.account.customer.respoitory;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.account.customer.domain.entity.Customer;

public interface CustomerJpaRepository extends JpaRepository<Customer,Long> {
}
