package com.nhnacademy.back.product.contributor.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestContributorDTO {
	@NotBlank
	private String contributorName;

	@NotNull
	private Long positionId;

}
