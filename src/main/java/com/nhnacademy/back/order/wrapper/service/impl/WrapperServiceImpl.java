package com.nhnacademy.back.order.wrapper.service.impl;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.nhnacademy.back.common.util.MinioUtils;
import com.nhnacademy.back.order.wrapper.domain.dto.request.RequestModifyWrapperDTO;
import com.nhnacademy.back.order.wrapper.domain.dto.request.RequestRegisterWrapperDTO;
import com.nhnacademy.back.order.wrapper.domain.dto.response.ResponseWrapperDTO;
import com.nhnacademy.back.order.wrapper.domain.entity.Wrapper;
import com.nhnacademy.back.order.wrapper.exception.WrapperNotFoundException;
import com.nhnacademy.back.order.wrapper.repository.WrapperJpaRepository;
import com.nhnacademy.back.order.wrapper.service.WrapperService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class WrapperServiceImpl implements WrapperService {
	private final WrapperJpaRepository wrapperJpaRepository;

	private final MinioUtils minioUtils;
	private final String BUCKET_NAME = "e2bs-wrappers-image";

	/**
	 * Wrapper를 DB에 저장하는 로직
	 */
	@Transactional
	@Override
	public void createWrapper(RequestRegisterWrapperDTO registerRequest) {
		String imagePath = "";
		MultipartFile wrapperImageFile = registerRequest.getWrapperImage();
		if (Objects.nonNull(wrapperImageFile) && !wrapperImageFile.isEmpty()) {
			imagePath = uploadFile(wrapperImageFile);
		}

		Wrapper wrapper = new Wrapper(registerRequest.getWrapperPrice(), registerRequest.getWrapperName(),
			imagePath, registerRequest.isWrapperSaleable());

		wrapperJpaRepository.save(wrapper);
	}

	/**
	 * DB에 저장 되어 있는 모든 Wrapper를 조회하여 List로 return 하는 로직
	 */
	@Override
	public Page<ResponseWrapperDTO> getWrappers(Pageable pageable) {
		log.info("포장지 전체 조회 서비스 시작");
		return wrapperJpaRepository.findAll(pageable)
			.map(wrapper -> new ResponseWrapperDTO(
				wrapper.getWrapperId(),
				wrapper.getWrapperPrice(),
				wrapper.getWrapperName(),
				wrapper.getWrapperImage().isEmpty() ? "" :
					minioUtils.getPresignedUrl(BUCKET_NAME, wrapper.getWrapperImage()),
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
				wrapper.getWrapperImage().isEmpty() ? "" :
					minioUtils.getPresignedUrl(BUCKET_NAME, wrapper.getWrapperImage()),
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

	/**
	 * 파일 업로드 메소드
	 */
	private String uploadFile(MultipartFile reviewImageFile) {
		String originalFilename = reviewImageFile.getOriginalFilename();
		UUID uuid = UUID.randomUUID();
		String objectName = uuid + "_" + originalFilename;
		minioUtils.uploadObject(BUCKET_NAME, objectName, reviewImageFile);
		return objectName;
	}
}
