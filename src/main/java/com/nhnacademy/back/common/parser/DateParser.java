package com.nhnacademy.back.common.parser;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;

public class DateParser {

	// 생성자 숨기기 (인스턴스화 방지)
	private DateParser() {
		throw new UnsupportedOperationException("기본 생성자 생성 금지");
	}

	public static LocalDate parseLocalDate(String birthdayMMdd) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMdd");
		MonthDay monthDay = MonthDay.parse(birthdayMMdd, formatter);
		return monthDay.atYear(2000);
	}

}
