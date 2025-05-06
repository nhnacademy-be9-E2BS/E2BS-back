package com.nhnacademy.back.account.address.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.account.address.domain.entity.Address;

public interface AddressJpaRepository extends JpaRepository<Address,Long> {
}
