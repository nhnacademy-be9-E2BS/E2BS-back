package com.nhnacademy.back.account.address.domain.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAddressDTO {

	private String addressAlias;
	private String addressCode;
	private String addressInfo;
	private String addressDetail;
	private String addressExtra;
	private boolean addressDefault;
	private LocalDateTime addressCreatedAt;
	private long addressId;

}
