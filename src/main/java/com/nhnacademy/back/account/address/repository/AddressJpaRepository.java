package com.nhnacademy.back.account.address.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nhnacademy.back.account.address.domain.entity.Address;

public interface AddressJpaRepository extends JpaRepository<Address, Long> {

	List<Address> getAddressesByMember_MemberId(String memberMemberId);

}
