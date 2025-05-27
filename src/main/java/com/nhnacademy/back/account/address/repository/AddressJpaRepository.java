package com.nhnacademy.back.account.address.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nhnacademy.back.account.address.domain.dto.UpdateAddressDTO;
import com.nhnacademy.back.account.address.domain.entity.Address;

public interface AddressJpaRepository extends JpaRepository<Address, Long> {

	List<Address> getAddressesByMember_MemberId(String memberMemberId);

	Address getAddressByAddressId(long addressId);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE Address a SET a.addressAlias = :#{#dto.addressAlias}, a.addressCode = :#{#dto.addressCode}, " +
		"a.addressInfo = :#{#dto.addressInfo}, a.addressDetail = :#{#dto.addressDetail}, " +
		"a.addressExtra = :#{#dto.addressExtra}, a.addressDefault = :#{#dto.addressDefault}, " +
		"a.addressCreatedAt = :#{#dto.addressCreatedAt} WHERE a.addressId = :#{#dto.addressId}")
	int updateAddress(@Param("dto") UpdateAddressDTO dto);

}
