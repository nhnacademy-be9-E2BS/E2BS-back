package com.nhnacademy.back.order.wrapper.domain.dto.response;

import java.util.List;

import com.nhnacademy.back.order.wrapper.domain.entity.Wrapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ResponseWrapperDTO {
	List<Wrapper> wrappers;
}
