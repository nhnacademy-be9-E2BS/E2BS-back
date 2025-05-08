package com.nhnacademy.back.order.wrapper.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.nhnacademy.back.order.wrapper.domain.dto.request.RequestWrapperDTO;
import com.nhnacademy.back.order.wrapper.domain.entity.Wrapper;

public interface WrapperService {
	void createWrapper(RequestWrapperDTO request);

	List<Wrapper> getWrappers(Pageable pageable);

	List<Wrapper> getWrappersBySaleable(boolean isSaleable, Pageable pageable);

	void updateWrapper(long wrapperId, RequestWrapperDTO request);
}
