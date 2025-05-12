package com.nhnacademy.back.order.wrapper.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nhnacademy.back.order.wrapper.domain.dto.request.RequestModifyWrapperDTO;
import com.nhnacademy.back.order.wrapper.domain.dto.request.RequestRegisterWrapperDTO;
import com.nhnacademy.back.order.wrapper.domain.dto.response.ResponseWrapperDTO;

public interface WrapperService {
	void createWrapper(RequestRegisterWrapperDTO registerRequest);

	Page<ResponseWrapperDTO> getWrappers(Pageable pageable);

	Page<ResponseWrapperDTO> getWrappersBySaleable(boolean isSaleable, Pageable pageable);

	void updateWrapper(long wrapperId, RequestModifyWrapperDTO modifyRequest);
}
