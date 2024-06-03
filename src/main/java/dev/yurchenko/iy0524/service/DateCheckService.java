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
		;
		long periodDaysTotal = ChronoUnit.DAYS.between(start, end);
		long periodWithoutWeekends = daysInRangeWithoutWeekends(start, end);
		long numberOfWeekends = periodDaysTotal - periodWithoutWeekends;
		int independenceDay = independenceDayInRange(start, end, holidayCharge);
		int laborDay = laborDayInRange(start, end, holidayCharge);
		long freeDays = laborDay + independenceDay
				               + (weekdayCharge ? 0 : periodWithoutWeekends)
				               + (weekendCharge ? 0 : numberOfWeekends);
		
		DateCheckout dateCheckout = new DateCheckout();
		dateCheckout.setDueDate(Date.from(end.atStartOfDay(ZoneId.systemDefault()).toInstant()));
		dateCheckout.setCheckoutDate(checkoutDate);
		dateCheckout.setRentalDays(periodDaysTotal);
		dateCheckout.setFreeDays(freeDays);
		dateCheckout.setChargeDays(periodDaysTotal - freeDays);
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
	
	//		Independence Day, July 4th - If falls on weekend, it is observed on the closest weekday (if Sat,
//			then Friday before, if Sunday, then Monday after)
	private int independenceDayInRange(LocalDate start, LocalDate end, Boolean holidayCharge) {
		if (holidayCharge) {
			return 0;
		}
		if(end.getYear() - start.getYear() > 0) {
		
		}
		//MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, and SATURDAY, SUNDAY
		LocalDate independenceDay = LocalDate.of(start.getYear(), Month.JULY, 4);
		if (independenceDay.getDayOfWeek().getValue() > 5) {
			if (start.getMonth() == Month.JULY || end.getMonth() == Month.JULY) {
				int dayOfWeek = independenceDay.getDayOfWeek().getValue();
				//if dayOfWeek == Sunday we make Monday holiday
				if (dayOfWeek == 7) {
					LocalDate mondayHoliday = independenceDay.plusDays(1);
					if (start.isEqual(mondayHoliday) || end.isEqual(mondayHoliday)
							    || start.isBefore(mondayHoliday) && end.isAfter(mondayHoliday)) {
						return 1;
					}
				}
				//if dayOfWeek == Saturday we make Friday holiday
				if (dayOfWeek == 6) {
					LocalDate fridayHoliday = independenceDay.minusDays(1);
					
					if (start.isEqual(fridayHoliday) || end.isEqual(fridayHoliday)
							    || (end.isAfter(fridayHoliday) && start.isBefore(fridayHoliday))) {
						return 1;
					}
				}
			}
			
		}else {
			if (start.isEqual(independenceDay) || end.isEqual(independenceDay)
					    || (end.isAfter(independenceDay) && start.isBefore(independenceDay))) {
					    return 1;
			}
		}
		
		return 0;
	}
	
	//		Labor Day - First Monday in September
	private int laborDayInRange(LocalDate start, LocalDate end, Boolean holidayCharge) {
		if (holidayCharge) {
			return 0;
		}
		LocalDate firstOfSeptember = LocalDate.of(start.getYear(), Month.SEPTEMBER, 1);
		LocalDate laborDay = firstOfSeptember.with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));
		if (start.isEqual(laborDay) || end.isEqual(laborDay) || (start.isBefore(laborDay) && end.isAfter(laborDay))) {
			return 1;
		}
		return 0;
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
