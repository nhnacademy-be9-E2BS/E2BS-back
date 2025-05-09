package com.nhnacademy.back.order.wrapper.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.order.wrapper.domain.entity.Wrapper;

public interface WrapperJpaRepository extends JpaRepository<Wrapper, Long> {
	/**
	 * 포장지의 판매 여부에 따른 리스트를 페이징 처리하여 반환하는 메소드
	 */
	Page<Wrapper> findAllByWrapperSaleable(boolean isSaleable, Pageable pageable);
}
