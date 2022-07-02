package me.straikerinacz.theapi.demo.test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class Main {

	public static void main(String[] args) {
		System.out.println((int)'l');
	}

	public static long getNextSchedule() {
		Date date = new Date();
		date.setHours(20);
		date.setMinutes(0);
		date.setSeconds(0);
		return Instant.now().until(date.toInstant(), ChronoUnit.SECONDS);
	}
}
