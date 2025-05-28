package com.nhnacademy.back.account.oauth.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCheckOAuthIdDTO {

	private boolean existsOAuthId;

}
