package com.nhnacademy.back.order.deliveryfee.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.order.deliveryfee.domain.DeliveryFee;

public interface DeliveryFeeJpaRepository extends JpaRepository<DeliveryFee,Long> {
}
