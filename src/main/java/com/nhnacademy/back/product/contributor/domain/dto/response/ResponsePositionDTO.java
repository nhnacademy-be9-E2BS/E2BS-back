package com.nhnacademy.back.product.contributor.domain.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

import com.nhnacademy.back.product.contributor.domain.entity.Position;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ResponsePositionDTO {
	private long positionId;
	private String positionName;

	public ResponsePositionDTO(String positionName) {
		this.positionName = positionName;
	}
}
