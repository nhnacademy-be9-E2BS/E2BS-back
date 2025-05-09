package com.nhnacademy.back.product.contributor.domain.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

import com.nhnacademy.back.product.contributor.domain.entity.Position;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ResponsePositionDTO {
	private String positionName;
}
