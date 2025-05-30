package com.nhnacademy.back.common.parser;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;

public class DateParser {

	public static LocalDate LocalDateParser(String birthdayMMdd) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMdd");
		MonthDay monthDay = MonthDay.parse(birthdayMMdd, formatter);
		return monthDay.atYear(2000);
	}

}
