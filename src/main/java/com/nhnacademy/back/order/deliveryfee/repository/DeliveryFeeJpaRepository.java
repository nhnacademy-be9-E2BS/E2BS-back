package com.nhnacademy.back.order.deliveryfee.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.order.deliveryfee.domain.entity.DeliveryFee;

public interface DeliveryFeeJpaRepository extends JpaRepository<DeliveryFee,Long> {
	Page<DeliveryFee> findAllByOrderByDeliveryFeeDateDesc(Pageable pageable);
	DeliveryFee findTopByOrderByDeliveryFeeDateDesc();
}
