package com.nhnacademy.back.coupon.membercoupon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.account.customer.domain.entity.Customer;
import com.nhnacademy.back.coupon.membercoupon.domain.entity.MemberCoupon;

public interface MemberCouponJpaRepository extends JpaRepository<MemberCoupon, Long> {

	List<MemberCoupon> getMemberCouponsByCustomer(Customer customer);

}
