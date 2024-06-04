package dev.yurchenko.iy0524.service;

import dev.yurchenko.iy0524.service.dto.DateCheckout;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

@Component
public class DateCheckService {
	
	
	public DateCheckout getCheckoutDateFromDate(String date,
	                                            int days,
	                                            Boolean weekdayCharge,
	                                            Boolean weekendCharge,
	                                            Boolean holidayCharge) {
		
		Date checkoutDate = getDateFromStringValue(date);
		
		LocalDate start = checkoutDate.toInstant()
				                  .atZone(ZoneId.systemDefault())
				                  .toLocalDate();
		LocalDate end = start.plusDays(days);
		
		long periodDaysTotal = ChronoUnit.DAYS.between(start, end);
		long periodWithoutWeekends = daysInRangeWithoutWeekends(start, end);
		long numberOfWeekends = periodDaysTotal - periodWithoutWeekends;
		//independenceDay count in range
//		int independenceDay = independenceDayInRange(start, end, holidayCharge);
		int independenceDay = countIndependenceDaysInRange(start, end);
		//labor day count in range
//		int laborDay = laborDayInRange(start, end, holidayCharge);
		int laborDay = countLaborDayInRange(start, end);
		long freeDays = ((holidayCharge) ? 0 : (!weekdayCharge ? 0 : (laborDay + independenceDay)))
				                + (weekdayCharge ? 0 : periodWithoutWeekends)
				                + (weekendCharge ? 0 : numberOfWeekends);
		
		DateCheckout dateCheckout = new DateCheckout(periodDaysTotal, periodDaysTotal - freeDays,
				freeDays, checkoutDate, Date.from(end.atStartOfDay(ZoneId.systemDefault()).toInstant()));
		return dateCheckout;
	}
	
	private long daysInRangeWithoutWeekends(LocalDate start, LocalDate end) {
		
		boolean startOnWeekend = false;
		boolean endOnWeekend = false;
		if (start.getDayOfWeek().getValue() > 5) {
			start = start.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
			startOnWeekend = true;
		}
		if (end.getDayOfWeek().getValue() > 5) {
			end = end.with(TemporalAdjusters.previous(DayOfWeek.FRIDAY));
			endOnWeekend = true;
		}
		long weeks = ChronoUnit.WEEKS.between(start, end);
		int addValue = startOnWeekend || endOnWeekend ? 1 : 0;
		
		return (weeks * 5) + addValue + (end.getDayOfWeek().getValue() - start.getDayOfWeek().getValue());
	}
	
	//			Independence Day, July 4th - If falls on weekend, it is observed on the closest weekday (if Sat,
//			then Friday before, if Sunday, then Monday after)
	private int countIndependenceDaysInRange(LocalDate start, LocalDate end) {
		int count = 0;
		if (end.getYear() - start.getYear() >= 0) {
			int year = start.getYear();
			while (year <= end.getYear()) {
				//MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, and SATURDAY, SUNDAY
				LocalDate independenceDay = LocalDate.of(year, Month.JULY, 4);
				if (independenceDay.getDayOfWeek().getValue() > 5) {
					int dayOfWeek = independenceDay.getDayOfWeek().getValue();
					//if dayOfWeek == Sunday we make Monday holiday
					if (dayOfWeek == 7) {
						count = getCountIndependenceDayOnDateRange(start, end, independenceDay.plusDays(1), count);
					}
					//if dayOfWeek == Saturday we make Friday holiday
					if (dayOfWeek == 6) {
						count = getCountIndependenceDayOnDateRange(start, end, independenceDay.minusDays(1), count);
					}
					
				} else {
					count = getCountIndependenceDayOnDateRange(start, end, independenceDay, count);
				}
				
				year++;
			}
		}
		
		return count;
	}
	
	private static int getCountIndependenceDayOnDateRange(LocalDate start, LocalDate end, LocalDate independenceDay, int count) {
		if (start.isEqual(independenceDay) || end.isEqual(independenceDay)
				    || (end.isAfter(independenceDay) && start.isBefore(independenceDay))) {
			count++;
		}
		return count;
	}
	
	//		Labor Day - First Monday in September
	private int countLaborDayInRange(LocalDate start, LocalDate end) {
		int year = start.getYear();
		int count = 0;
		while (end.getYear() - year >= 0) {
			LocalDate firstOfSeptember = LocalDate.of(year, Month.SEPTEMBER, 1);
			LocalDate laborDay = firstOfSeptember.with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));
			if (start.isEqual(laborDay) || end.isEqual(laborDay) || (start.isBefore(laborDay) && end.isAfter(laborDay))) {
				count++;
			}
			year++;
		}
		return count;
	}
	
	private Date getDateFromStringValue(String date) {
		DateFormat dform = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return dform.parse(date);
		} catch (ParseException e) {
			throw new IllegalArgumentException("Checkout date can not be parsed correctly");
		}
		
	}
	
	
}
