package com.nhnacademy.back.order.wrapper.service.impl;

import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.back.order.wrapper.domain.dto.request.RequestModifyWrapperDTO;
import com.nhnacademy.back.order.wrapper.domain.dto.request.RequestRegisterWrapperDTO;
import com.nhnacademy.back.order.wrapper.domain.dto.response.ResponseWrapperDTO;
import com.nhnacademy.back.order.wrapper.domain.entity.Wrapper;
import com.nhnacademy.back.order.wrapper.exception.WrapperNotFoundException;
import com.nhnacademy.back.order.wrapper.repository.WrapperJpaRepository;
import com.nhnacademy.back.order.wrapper.service.WrapperService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WrapperServiceImpl implements WrapperService {
	private final WrapperJpaRepository wrapperJpaRepository;

	/**
	 * Wrapper를 DB에 저장하는 로직
	 */
	@Transactional
	@Override
	public void createWrapper(RequestRegisterWrapperDTO registerRequest) {
		Wrapper wrapper = new Wrapper(registerRequest.getWrapperPrice(), registerRequest.getWrapperName(),
			registerRequest.getWrapperImage(), registerRequest.isWrapperSaleable());

		wrapperJpaRepository.save(wrapper);
	}

	/**
	 * DB에 저장 되어 있는 모든 Wrapper를 조회하여 List로 return 하는 로직
	 */
	@Override
	public Page<ResponseWrapperDTO> getWrappers(Pageable pageable) {
		return wrapperJpaRepository.findAll(pageable)
			.map(wrapper -> new ResponseWrapperDTO(
				wrapper.getWrapperId(),
				wrapper.getWrapperPrice(),
				wrapper.getWrapperName(),
				wrapper.getWrapperImage(),
				wrapper.isWrapperSaleable()
			));
	}

	/**
	 * DB에 저장 되어 있는 Wrapper 중에서 wrapper_saleable 값에 따른 모든 Wrapper를 조회하여 List로 return 하는 로직
	 * 고객이 선택 가능한 포장지 리스트를 조회할 때 사용 (isSaleable = true)
	 */
	@Override
	public Page<ResponseWrapperDTO> getWrappersBySaleable(boolean isSaleable, Pageable pageable) {
		return wrapperJpaRepository.findAllByWrapperSaleable(isSaleable, pageable)
			.map(wrapper -> new ResponseWrapperDTO(
				wrapper.getWrapperId(),
				wrapper.getWrapperPrice(),
				wrapper.getWrapperName(),
				wrapper.getWrapperImage(),
				wrapper.isWrapperSaleable()
			));
	}

	/**
	 * DB에 저장 되어 있는 Wrapper의 값을 수정하는 로직
	 * 수정 가능한 값 : wrapper_price, wrapper_name, wrapper_image, wrapper_saleable
	 */
	@Transactional
	@Override
	public void updateWrapper(long wrapperId, RequestModifyWrapperDTO modifyRequest) {
		if (Objects.isNull(modifyRequest)) {
			throw new IllegalArgumentException();
		}
		Optional<Wrapper> wrapper = wrapperJpaRepository.findById(wrapperId);
		if (wrapper.isEmpty()) {
			throw new WrapperNotFoundException("wrapper not found, id: %d".formatted(wrapperId));
		}

		wrapper.get().setWrapper(modifyRequest.isWrapperSaleable());
		wrapperJpaRepository.save(wrapper.get());
	}
}
