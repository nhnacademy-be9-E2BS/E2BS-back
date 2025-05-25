package com.nhnacademy.back.home.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseHomeMemberNameDTO {

	private String memberId;
	private String memberName;

}
