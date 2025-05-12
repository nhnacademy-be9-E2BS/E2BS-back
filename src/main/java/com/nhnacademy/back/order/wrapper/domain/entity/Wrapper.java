package com.nhnacademy.back.order.wrapper.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wrapper {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long wrapperId;

	@Column(nullable = false)
	private long wrapperPrice;

	@Column(nullable = false, length = 30)
	private String wrapperName;

	@Column(nullable = false)
	private String wrapperImage;

	@Column(nullable = false)
	private boolean wrapperSaleable;

	public Wrapper(long wrapperPrice, String wrapperName, String wrapperImage, boolean wrapperSaleable) {
		this.wrapperPrice = wrapperPrice;
		this.wrapperName = wrapperName;
		this.wrapperImage = wrapperImage;
		this.wrapperSaleable = wrapperSaleable;
	}

	public void setWrapper(boolean wrapperSaleable) {
		this.wrapperSaleable = wrapperSaleable;
	}

}
