package com.nhnacademy.back.account.address.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.nhnacademy.back.account.address.domain.entity.Address;

public interface AddressJpaRepository extends JpaRepository<Address, Long> {

	List<Address> getAddressesByMember_MemberId(String memberMemberId);

	Address getAddressByAddressId(long addressId);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE Address a SET a.addressAlias = :addressAlias, a.addressCode = :addressCode, a.addressInfo = :addressInfo, a.addressDetail = :addressDetail, a.addressExtra = :addressExtra, a.addressDefault = :addressDefault, a.addressCreatedAt = :addressCreatedAt WHERE a.addressId = :addressId")
	int updateAddress(
		String addressAlias, String addressCode, String addressInfo,
		String addressDetail, String addressExtra, boolean addressDefault,
		LocalDateTime addressCreatedAt, long addressId
	);

}
