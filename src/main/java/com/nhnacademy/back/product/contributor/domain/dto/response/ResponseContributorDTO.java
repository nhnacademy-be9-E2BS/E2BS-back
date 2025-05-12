package com.nhnacademy.back.product.contributor.domain.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ResponseContributorDTO {
	@NotNull
	private Long contributorId;
	@NotBlank
	private String contributorName;

	@NotNull
	private Long positionId;
	@NotBlank
	private String positionName;

	public ResponseContributorDTO(String positionName, String contributorName) {
		this.positionName = positionName;
		this.contributorName = contributorName;
	}
}
