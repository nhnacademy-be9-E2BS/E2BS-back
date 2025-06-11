package com.nhnacademy.back.account.oauth.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestOAuthLoginDTO {

	@NotNull
	private String memberId;

}
