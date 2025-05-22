package com.nhnacademy.back.product.contributor.domain.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class RequestProductContributorCreateDTO {
	private String productIsbn;
	private List<String> contributorName;
}
