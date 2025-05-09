package com.nhnacademy.back.order.wrapper.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.back.order.wrapper.domain.dto.request.RequestWrapperDTO;
import com.nhnacademy.back.order.wrapper.domain.dto.response.ResponseWrapperDTO;

public interface WrapperService {
	void createWrapper(RequestWrapperDTO request);

	Page<ResponseWrapperDTO> getWrappers(Pageable pageable);

	Page<ResponseWrapperDTO> getWrappersBySaleable(boolean isSaleable, Pageable pageable);

	void updateWrapper(long wrapperId, RequestWrapperDTO request);
}
