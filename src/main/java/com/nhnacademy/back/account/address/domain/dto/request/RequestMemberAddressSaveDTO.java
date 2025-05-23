package com.nhnacademy.back.account.address.domain.dto.request;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestMemberAddressSaveDTO {

	private String addressAlias;
	@NotNull
	@Length(max = 5)
	private String addressCode;
	private String addressInfo;
	@NotNull
	private String addressDetail;
	@NotNull
	private String addressExtra;
	@NotNull
	private boolean addressDefault;

}
